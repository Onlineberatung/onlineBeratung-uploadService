package de.caritas.cob.uploadservice.api.exception;

public class CustomCryptoException extends RuntimeException {

  private static final long serialVersionUID = 1232112575979020932L;

  /**
   * Exception when something with the encryption goes wrong.
   *
   * @param ex
   */
  public CustomCryptoException(Exception ex) {
    super(ex);
  }

}
