package com.tellyouiam.alittlebitaboutspring.exception;

import com.tellyouiam.alittlebitaboutspring.utils.error.ErrorInfo;

public class CustomException extends Exception {

    private static final long serialVersionUID = 1L;

    private ErrorInfo error;

    public CustomException() {
    }

    public CustomException(Throwable cause) {
        super(cause);
        this.setError(ErrorInfo.INTERNAL_SERVER_ERROR);
    }

    public CustomException(ErrorInfo error) {
        this.setError(error);
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        if (error != null) return error.getMessage();
        return super.getMessage();
    }
}
