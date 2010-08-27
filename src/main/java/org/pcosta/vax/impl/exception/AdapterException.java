package org.pcosta.vax.impl.exception;

public class AdapterException extends VaxException {

    private static final long serialVersionUID = -3045783350899325255L;

    public AdapterException() {
        super();
    }

    public AdapterException(final String message) {
        super(message);
    }

    public AdapterException(final Throwable cause) {
        super(cause);
    }

    public AdapterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
