package org.peletomi.vax.impl.exception;

import java.util.List;

public class ValidationException extends VaxException {

    private static final long serialVersionUID = -3045783350899325255L;

    private List<String> violations;

    public ValidationException() {
        super();
    }

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final Throwable cause) {
        super(cause);
    }

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public List<String> getViolations() {
        return violations;
    }

    public void setViolations(final List<String> violations) {
        this.violations = violations;
    }
}
