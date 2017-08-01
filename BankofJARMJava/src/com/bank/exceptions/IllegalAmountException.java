package com.bank.exceptions;

public class IllegalAmountException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Call Exception constructor with no message
   */
  public IllegalAmountException() {
	super("");
  }
  
  /**
   * Call Exception constructor with a message
   * @param message is message to send to Exception constructor
   */
  public IllegalAmountException(String message) {
	super(message);
  }
  
}
