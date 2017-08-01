package com.b07.bankofjarm.exceptions;

public class InsufficientFundsException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Call Exception constructor with no message
   */
  public InsufficientFundsException() {
	super("");
  }
  
  /**
   * Call Exception constructor with a message
   * @param message is message to send to Exception constructor
   */
  public InsufficientFundsException(String message) {
	super(message);
  }

}
