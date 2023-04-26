package antigravity.error.exception;

import antigravity.error.ErrorCode;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

	private static final long serialVersionUID = 1L;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
