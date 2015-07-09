package com.secucard.connect;

/**
 * This exception wraps business related errors thrown by the secucard server.
 * The status field may contain additional error details.
 */
public class ServerErrorException extends RuntimeException {
  private String code = "500"; // default for any unknown internal error
  private String userMessage;
  private String supportId;
  private String serverError;

  /**
   * Returns an unique error code.
   */
  public String getCode() {
    return code;
  }

  /**
   * Return a user friendly message describing the problem.
   */
  public String getUserMessage() {
    return userMessage;
  }

  /**
   * Returns an unique error id to provide to the support.
   */
  public String getSupportId() {
    return supportId;
  }

  /**
   * Returns message describing the problem.
   */
  @Override
  public String getMessage() {
    return super.getMessage();
  }

  /**
   * Returns the original error type the server was submitting.
   * May give additional hints about the problem.
   */
  public String getServerError() {
    return serverError;
  }

  public ServerErrorException(String message) {
    super(message);
  }

  public ServerErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServerErrorException(String code, String message, String userMessage, String serverError, String supportId,
                              Throwable cause) {
    super(message, cause);
    this.code = code;
    this.userMessage = userMessage;
    this.serverError = serverError;
    this.supportId = supportId;
  }


  @Override
  public String toString() {
    return getClass().getName() + ": " + "code='" + code + '\'' +
        ", message='" + getMessage() + '\'' +
        ", userMessage='" + userMessage + '\'' +
        ", supportId='" + supportId + '\'' +
        ", serverError='" + serverError + '\'' +
        "} ";
  }


}
