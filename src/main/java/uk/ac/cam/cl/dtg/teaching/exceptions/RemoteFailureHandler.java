package uk.ac.cam.cl.dtg.teaching.exceptions;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class captures InternalServerErrorExceptions, extracts the response from
 * the server and repackages it as a SerializableException
 * <p>
 * When you use a proxy object to call a remote api any failure at the other end
 * will result in an internal server error (500). This causes the proxy object
 * to throw an InternalServerErrorException. The purpose of this handler is to
 * capture this exception and extract the original cause from it. This could be
 * in two forms: 1) the remote end has returned a JSON serialisation of
 * SerializableException - in this case we just deserialise it. 2) the remote
 * end is really broken and returns an HTML error message from the container -
 * in this case we invent an exception by attempting to parse the remote text.
 * <p>
 * After recovering the exception we add our own stack trace to the remote stack
 * trace in order to record information about what we did to cause the proxy
 * call.  
 * <p>
 * We then serialise this new SerializableException as JSON to the client.
 * <p> 
 * This class should be registered with your resteasy application.
 * 
 * @author acr31
 * 
 */
@Provider
public class RemoteFailureHandler implements
		ExceptionMapper<InternalServerErrorException> {

	private static Logger log = LoggerFactory
			.getLogger(RemoteFailureHandler.class);

	@Override
	public Response toResponse(InternalServerErrorException t) {
		Throwable message = appendStackTrace(readException(t), t);
		return Response.serverError().entity(message)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Append the local stack trace onto the remote one. This then captures the
	 * sequence of operations that happened before we caused the error on the
	 * remote machine. This is different to using setCause because the remote
	 * exception could well already have cause information which arose at the
	 * remote site. There is no message to add from the local exception because
	 * this handler only deals with ClientResponseFailures.
	 * 
	 * @param remoteException the exception recovered from the remote end
	 * @param localException the local exception which we are handling
	 * @return a new SerializableException representing both
	 */
	public static SerializableException appendStackTrace(
			SerializableException remoteException,
			InternalServerErrorException localException) {

		SerializableStackTraceElement[] remoteStack = remoteException
				.getSerializableStackTrace();
		StackTraceElement[] localStack = localException.getStackTrace();
		SerializableStackTraceElement[] newStack = new SerializableStackTraceElement[remoteStack.length
				+ localStack.length];
		int ptr = 0;
		for (SerializableStackTraceElement s : remoteStack) {
			newStack[ptr++] = s;
		}
		for (StackTraceElement s : localStack) {
			newStack[ptr++] = new SerializableStackTraceElement(s);
		}
		remoteException.setSerializableStackTrace(newStack);
		return remoteException;
	}

	/**
	 * If a remote error occurs then the remote end should send a
	 * SerializableException out over json. This method tries to recover that
	 * object. If the content type isn't application/json we assume something
	 * went very wrong at the other end and just package up the response in a
	 * new Exception
	 * 
	 * @param e
	 *            the InternalServerErrorException which we've caught
	 * @return a SerializableException collected from the remote server
	 */
	public static SerializableException readException(
			InternalServerErrorException e) {
		Response clientResponse = e.getResponse();
		String contentType = clientResponse.getHeaders()
				.getFirst("Content-Type").toString();
		if (contentType.startsWith("application/json")) {
			// if they've sent us json then we'll assume they stuck with the API
			// contract and have sent an ApiFailureMessage

			SerializableException message = (SerializableException) clientResponse
					.readEntity(SerializableException.class);

			return message;
		}

		String message = (String) e.getResponse().readEntity(String.class);
		if (contentType.startsWith("text/html")) {
			// we've got back some html from the server - something has gone
			// wrong since it should be giving us JSON
			// the best we can do is load it as a string, strip out everything
			// except the body of the document and rethrow that

			int openBodyTag = message.indexOf("<body>");
			int closeBodyTag = message.indexOf("</body>");
			if (openBodyTag != -1 && closeBodyTag != -1) {
				message = message.substring(openBodyTag + 6, closeBodyTag);
			}
		} else {
			log.error("Unexpected Content-Type {} in error message",
					contentType);
		}

		return new SerializableException(new Exception(message));
	}

}
