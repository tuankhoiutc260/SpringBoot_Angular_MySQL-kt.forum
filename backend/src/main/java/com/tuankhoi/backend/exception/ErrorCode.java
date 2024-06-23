package com.tuankhoi.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
//    USER
    USER_EMAIL_INVALID(1001, "Email should be valid"),
    USER_EMAIL_NOTBLANK(1001, "Email cannot be null"),
    USER_PASSWORD_INVALID(1001, "Password must be at least 8 characters"),
    USER_NAME_NOTBLANK(1001, "Role ID cannot be null"),
    USER_EXISTED(1001, "User existed"),
    USER_NOTFOUND(1001, "User not found"),

//    ROLE
    ROLE_EXISTED(1001, "User existed"),
    ROLE_NOTFOUND(1001, "Role not found"),
    ROLE_NOTNULL(1001, "Role ID cannot be null"),

//    POST
    POST_TITLE_NOTBLANK(1001, "Title cannot be blank"),
    POST_TITLE_INVALID(1001, "Title cannot be longer than 255 characters"),
    POST_CONTENT_NOTBLANK(1001, "Content cannot be blank"),
    POST_TAGS_NOTEMPTY(1001, "Tags cannot be empty"),
    POST_NOTFOUND(1001, "Post not found")
    ;


    private int code;
    private String message;
}
