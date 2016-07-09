package uk.ac.cam.cl.dtg.teaching.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class makes sure that all exceptions are serialised as JSON in a format
 * which we understand.
 * <p>
 * Normally when an exception is thrown by resteasy code this results in an
 * error message in the server logs only. This exception handler ensures that
 * any exception is packaged (as a SerializableException) and then sent to the
 * client as JSON.
 * <p>
 * This should be registered with resteasy in your application class. 
 * 
 * @author acr31
 * 
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {

	protected Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);
	
	@Context
	private HttpServletRequest request;

	@Override
	public Response toResponse(Throwable exception) {
		LOG.info("Throwing exception to client",exception);
		int statusCode = 500;
		if (exception instanceof HttpStatusCode404) {
			statusCode = 404;
		}
		return Response.status(statusCode)
				.entity(new SerializableException(exception,requestToHost(request)))
				.type(MediaType.APPLICATION_JSON).build();
	}
	
	public static String requestToHost(HttpServletRequest request) {
		return request.getServerName()+":"+request.getServerPort()+request.getRequestURI();
	}
}
