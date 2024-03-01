package me.croco.springbootdeveloper.config.error.exception;

import me.croco.springbootdeveloper.config.error.ErrorCode;

public class NotFoundException extends BusinessBaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }


}
