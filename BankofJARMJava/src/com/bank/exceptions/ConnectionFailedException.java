package com.bank.exceptions;

public class ConnectionFailedException extends Exception {

  private static final long serialVersionUID = 1L;
  
  /**
   * Call Exception constructor with no message
   */
  public ConnectionFailedException() {
	super("");
  }
  
  /**
   * Call Exception constructor with a message
   * @param message is message to send to Exception constructor
   */
  public ConnectionFailedException(String message) {
	super(message);
  }

}
