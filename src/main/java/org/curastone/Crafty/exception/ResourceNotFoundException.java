package org.curastone.Crafty.exception;

/** A custom exception type for the case when the requested resource is not found in the system. */
public class ResourceNotFoundException extends Exception {

  public ResourceNotFoundException() {
    super("The requested resource is not found");
  }
}
