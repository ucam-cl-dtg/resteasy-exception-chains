package uk.ac.cam.cl.dtg.teaching.exceptions;

/**
 * A class containing exception information which can be serialized to JSON and deserialized again.
 * 
 * @author acr31
 *
 */
class SerializableException  {

	private String className;

	private String message;

	private SerializableException cause;

	private SerializableStackTraceElement[] stackTrace;
	
	SerializableException() {
	}

	SerializableException(Throwable toSerialize,String host) {
		this.message = toSerialize.getMessage();
		this.className = toSerialize.getClass().getName();
		if (toSerialize.getCause() != null) {
			this.cause = new SerializableException(toSerialize.getCause(),host);
		}
		StackTraceElement[] stackTrace = toSerialize.getStackTrace();
		this.stackTrace = new SerializableStackTraceElement[stackTrace.length];
		for (int i = 0; i < stackTrace.length; ++i) {
			this.stackTrace[i] = new SerializableStackTraceElement(stackTrace[i],host);
		}
	}

	SerializableException(
			String className,
			String message,
			SerializableException cause,
			SerializableStackTraceElement[] stackTrace) {
		super();
		this.className = className;
		this.message = message;
		this.cause = cause;
		this.stackTrace = stackTrace;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public SerializableStackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(
			SerializableStackTraceElement[] serializableStackTrace) {
		this.stackTrace = serializableStackTrace;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCause(SerializableException cause) {
		this.cause = cause;
	}

	public String getMessage() {
		return this.message;
	}

	public synchronized SerializableException getCause() {
		return this.cause;
	}
	
	@Override
	public String toString() {
		String message = getMessage();
		return this.className + (message == null ? "" : ": " + message);
	}
}
