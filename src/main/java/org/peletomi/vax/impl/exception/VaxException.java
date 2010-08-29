package org.peletomi.vax.impl.exception;

public class VaxException extends RuntimeException {

    private static final long serialVersionUID = -3045783350899325255L;

    public VaxException() {
        super();
    }

    public VaxException(final String message) {
        super(message);
    }

    public VaxException(final Throwable cause) {
        super(cause);
    }

    public VaxException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
