package me.croco.springbootdeveloper.config.error.exception;

import me.croco.springbootdeveloper.config.error.ErrorCode;

public class ArticleNotFoundException extends NotFoundException {
    public ArticleNotFoundException() {
        super(ErrorCode.ARTICLE_NOT_FOUND);
    }
}
