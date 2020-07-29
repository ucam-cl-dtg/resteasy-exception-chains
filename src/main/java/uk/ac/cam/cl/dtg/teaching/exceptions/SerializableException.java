package uk.ac.cam.cl.dtg.teaching.exceptions;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * A class containing exception information which can be serialized to JSON and deserialized again.
 *
 * @author acr31
 */
public class SerializableException {

  private String className;

  private String message;

  private SerializableException cause;

  private SerializableStackTraceElement[] stackTrace;

  public SerializableException() {}

  private static String getHostName() {
    try {
      return Inet4Address.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new Error("Failed to find name for local host", e);
    }
  }

  public SerializableException(Throwable toSerialize) {
    this(toSerialize, getHostName());
  }

  public SerializableException(Throwable toSerialize, String host) {
    try {
      this.message = toSerialize.getMessage();
    } catch (Throwable e) {
      this.message =
          toSerialize.getClass().getName() + ".getMessage() threw " + e.getClass().getName();
    }
    this.className = toSerialize.getClass().getName();
    if (toSerialize.getCause() != null) {
      this.cause = new SerializableException(toSerialize.getCause(), host);
    }
    StackTraceElement[] stackTrace = toSerialize.getStackTrace();
    this.stackTrace = new SerializableStackTraceElement[stackTrace.length];
    for (int i = 0; i < stackTrace.length; ++i) {
      this.stackTrace[i] = new SerializableStackTraceElement(stackTrace[i], host);
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

  public void setStackTrace(SerializableStackTraceElement[] serializableStackTrace) {
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
