package hu.bme.mit.inf.petridotnet.spdn;

public class SpdnException extends RuntimeException {
    public SpdnException() {
    }

    public SpdnException(String message) {
        super(message);
    }

    public SpdnException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpdnException(Throwable cause) {
        super(cause);
    }

    public SpdnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
