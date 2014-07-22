package uk.ac.cam.cl.dtg.teaching.exceptions;


/**
 * An implementation of StackTraceElement which can be serialized to JSON and back again.  This is required by the SeralizsbleException class.
 * @author acr31
 *
 */
class SerializableStackTraceElement {

	private String className;

	private String methodName;

	private String fileName;

	private int lineNumber;

	private String host;
	
	SerializableStackTraceElement(
			String className,
			String methodName,
			String fileName,
			int lineNumber,
			String host) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
		this.host = host;
	}

	SerializableStackTraceElement(StackTraceElement e, String host) {
		this.className = e.getClassName();
		this.methodName = e.getMethodName();
		this.fileName = e.getFileName();
		this.lineNumber = e.getLineNumber();
		this.host = host;
	}

	SerializableStackTraceElement() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(className + "." + methodName);
		if (host != null) {
			b.append("@"+host);
		}
		if (lineNumber == -2)
			b.append("(Native Method");
		else if (fileName != null && lineNumber >= 0)
			b.append("(" + fileName + ":" + lineNumber + ")");
		else
			b.append("(Unknown Source)");
		return b.toString();
	}
}