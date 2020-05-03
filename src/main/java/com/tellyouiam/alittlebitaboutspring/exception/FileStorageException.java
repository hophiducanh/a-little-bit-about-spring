package com.tellyouiam.alittlebitaboutspring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//This ensures that Spring boot responds with a 404 Not Found status when this exception is thrown.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
