package com.idolcraft.mail.common;

public class MailException extends RuntimeException {
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
