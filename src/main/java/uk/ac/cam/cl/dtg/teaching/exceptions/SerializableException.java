package uk.ac.cam.cl.dtg.teaching.exceptions;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * An implementation of Exception which can be serialized to JSON and deserialized again.
 * 
 * @author acr31
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SerializableException extends Exception {

	private static final long serialVersionUID = 759370006263359407L;

	@JsonSerialize
	private String className;

	@JsonSerialize
	private String message;

	@JsonSerialize
	private SerializableException cause;

	@JsonSerialize()
	private SerializableStackTraceElement[] serializableStackTrace;

	public SerializableException() {
	}

	public SerializableException(Throwable toSerialize) {
		this.message = toSerialize.getMessage();
		this.className = toSerialize.getClass().getName();
		if (toSerialize.getCause() != null) {
			this.cause = new SerializableException(toSerialize.getCause());
		}
		setStackTrace(toSerialize.getStackTrace());
	}

	@JsonCreator
	public SerializableException(
			@JsonProperty("className") String className,
			@JsonProperty("message") String message,
			@JsonProperty("cause") SerializableException cause,
			@JsonProperty("serializableStackTrace") SerializableStackTraceElement[] stackTrace) {
		super();
		this.className = className;
		this.message = message;
		this.cause = cause;
		this.serializableStackTrace = stackTrace;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public SerializableStackTraceElement[] getSerializableStackTrace() {
		return serializableStackTrace;
	}

	public void setSerializableStackTrace(
			SerializableStackTraceElement[] serializableStackTrace) {
		this.serializableStackTrace = serializableStackTrace;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCause(SerializableException cause) {
		this.cause = cause;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getLocalizedMessage() {
		return this.message;
	}

	@Override
	public synchronized Throwable getCause() {
		return this.cause;
	}

	@Override
	public synchronized Throwable initCause(Throwable cause) {
		this.cause = new SerializableException(cause);
		return this;
	}

	@Override
	public String toString() {
		String message = getLocalizedMessage();
		return this.className + (message == null ? "" : ": " + message);
	}

	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	@Override
	public void printStackTrace(PrintStream s) {
		printStackTrace(new PrintWriter(new OutputStreamWriter(s)));
	}

	@Override
	public void printStackTrace(PrintWriter writer) {
		synchronized (writer) {
			writer.println(toString());
			for (SerializableStackTraceElement s : serializableStackTrace) {
				writer.println("\tat " + s);
			}
			writer.flush();
		}
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		StackTraceElement[] result = new StackTraceElement[serializableStackTrace.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = new StackTraceElement(
					serializableStackTrace[i].getClassName(),
					serializableStackTrace[i].getMethodName(),
					serializableStackTrace[i].getFileName(),
					serializableStackTrace[i].getLineNumber());
		}
		return result;
	}

	@Override
	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.serializableStackTrace = new SerializableStackTraceElement[stackTrace.length];
		for (int i = 0; i < stackTrace.length; ++i) {
			this.serializableStackTrace[i] = new SerializableStackTraceElement(stackTrace[i]);
		}
	}
}
