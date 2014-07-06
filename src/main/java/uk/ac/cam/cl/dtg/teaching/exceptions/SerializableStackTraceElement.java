package uk.ac.cam.cl.dtg.teaching.exceptions;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * An implementation of StackTraceElement which can be serialized to JSON and back again.  This is required by the SeralizsbleException class.
 * @author acr31
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public class SerializableStackTraceElement {

	@JsonSerialize
	private String className;

	@JsonSerialize
	private String methodName;

	@JsonSerialize
	private String fileName;

	@JsonSerialize
	private int lineNumber;

	@JsonCreator
	public SerializableStackTraceElement(
			@JsonProperty("className") String className,
			@JsonProperty("methodName") String methodName,
			@JsonProperty("fileName") String fileName,
			@JsonProperty("lineNumber") int lineNumber) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.fileName = fileName;
		this.lineNumber = lineNumber;
	}

	public SerializableStackTraceElement(StackTraceElement e) {
		this.className = e.getClassName();
		this.methodName = e.getMethodName();
		this.fileName = e.getFileName();
		this.lineNumber = e.getLineNumber();
	}

	public SerializableStackTraceElement() {
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

	public boolean isNativeMethod() {
		return lineNumber == -2;
	}

	public String toString() {
		StringBuilder b = new StringBuilder(className + "." + methodName);
		if (isNativeMethod())
			b.append("(Native Method");
		else if (fileName != null && lineNumber >= 0)
			b.append("(" + fileName + ":" + lineNumber + ")");
		else
			b.append("(Unknown Source)");
		return b.toString();
	}
}