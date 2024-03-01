package me.croco.springbootdeveloper.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
 * 에러 메세지용 enum
 * 에러코드를 한 곳에 모아 관리하기 위한 enum
 *
 * 어떤 키값이 있는지 확인하기 좋음
 * 구조를 바꾸기 용이
 */
@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E2", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E3", "서버 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E4", "존재하지 않는 엔티티입니다."),

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "A1", "존재하지 않는 아티클입니다.");

    private final String message;

    private final String code;

    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
