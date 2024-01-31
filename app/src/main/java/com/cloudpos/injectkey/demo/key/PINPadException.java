package com.cloudpos.injectkey.demo.key;

public class PINPadException extends Exception {
    private static final long serialVersionUID = 1L;

    public PINPadException(String msg) {
        super(msg);
    }

    public PINPadException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
