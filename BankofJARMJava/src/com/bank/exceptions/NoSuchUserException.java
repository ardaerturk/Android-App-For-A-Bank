package com.bank.exceptions;

public class NoSuchUserException extends Exception {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Call Exception constructor with no message
   */
  public NoSuchUserException() {
    super("");
  }
  
  /**
   * Call Exception constructor with a message
   * @param message is message to send to Exception constructor
   */
  public NoSuchUserException(String message) {
    super(message);
  }

}
