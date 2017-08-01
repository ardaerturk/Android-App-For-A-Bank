package com.bank.exceptions;

public class NoSuchAccountException extends Exception {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Call Exception constructor with no message
   */
  public NoSuchAccountException() {
    super("");
  }
  
  /**
   * Call Exception constructor with a message
   * @param message is message to send to Exception constructor
   */
  public NoSuchAccountException(String message) {
    super(message);
  }

}
