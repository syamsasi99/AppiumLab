package io.github.syamsasi.appium_lab.exception;

/** Created by Syam Sasi on May, 2018 */
public class AppiumLabException extends Exception {
  public AppiumLabException() {}

  public AppiumLabException(String message) {
    super(message);
  }

  public AppiumLabException(String message, String arg) {
    super(message.replace("{}", arg));
  }

  public AppiumLabException(Throwable cause) {
    super(cause);
  }

  public AppiumLabException(String message, Throwable cause) {
    super(message, cause);
  }
}
