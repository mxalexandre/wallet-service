package com.recargapay.walletservice.exception;

import org.springframework.http.HttpStatus;

public class WalletAlreadyExistsException extends ApiException {

  public WalletAlreadyExistsException(String ownerDocument) {
    super("Wallet already exists for this document: " + ownerDocument, HttpStatus.CONFLICT);
  }
}
