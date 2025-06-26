package org.example.jwtsecurity.exception.authentication;

public class AlreadyRegisteredException extends RuntimeException {

  public AlreadyRegisteredException(String message) {
    super(message);
  }

}
