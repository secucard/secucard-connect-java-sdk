package com.secucard.connect.auth;

/**
 * Returns if a pending authentication process should be canceled or not.
 */
public interface CancelCallback {
  boolean cancel();
}
