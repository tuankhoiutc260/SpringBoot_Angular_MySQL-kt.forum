package com.tuankhoi.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // Uncategorized error - 500
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_VIOLATION(9998, "Data integrity violation", HttpStatus.BAD_REQUEST),

    // USER
    USER_EMAIL_INVALID(1001, "Email should be valid", HttpStatus.BAD_REQUEST),
    USER_EMAIL_NOTBLANK(1002, "Email cannot be null", HttpStatus.BAD_REQUEST),
    USER_NAME_NOTBLANK(1002, "Username cannot be null", HttpStatus.BAD_REQUEST),
    USER_NAME_INVALID(1003, "Username must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_NOTBLANK(1002, "Password cannot be null", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_INVALID(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1005, "User existed", HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTED(1006, "Email existed", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(1007, "User not found", HttpStatus.NOT_FOUND),
    USER_USERNAME_PASSWORD_INVALID(1008, "Incorrect Username or Password", HttpStatus.UNAUTHORIZED),

    // ROLE
    ROLE_EXISTED(2001, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOTFOUND(2002, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_NOTNULL(2003, "Role ID cannot be null", HttpStatus.BAD_REQUEST),
    ROLE_NAME_NOTBLANK(2004, "Role name cannot be blank", HttpStatus.BAD_REQUEST),

    // POST
    POST_TITLE_NOTBLANK(3001, "Title cannot be blank", HttpStatus.BAD_REQUEST),
    POST_TITLE_INVALID(3002, "Title cannot be longer than 255 characters", HttpStatus.BAD_REQUEST),
    POST_CONTENT_NOTBLANK(3003, "Content cannot be blank", HttpStatus.BAD_REQUEST),
    POST_TAGS_NOTEMPTY(3004, "Tags cannot be empty", HttpStatus.BAD_REQUEST),
    POST_NOTFOUND(3005, "Post not found", HttpStatus.NOT_FOUND),
    COMMENT_CONTENT_NOTBLANK(3006, "Comment content cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_POST_ID_NOTBLANK(3007, "Post ID of Comment cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_PARENT_ID_NOTBLANK(3008, "Comment Parent ID cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(3009, "Comment not found", HttpStatus.NOT_FOUND),
    PARENT_COMMENT_NOTFOUND(3010, "Parent Comment not found", HttpStatus.NOT_FOUND),
    POST_ID_NOTBLANK(3011, "Post ID cannot be blank", HttpStatus.BAD_REQUEST),


    // PERMISSION
    PERMISSION_EXISTED(4001, "Permission existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOTFOUND(4002, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_NAME_NOTBLANK(4003, "Permission cannot be blank", HttpStatus.BAD_REQUEST),
    PERMISSION_DESCRIPTION_NOTBLANK(4004, " Permission description cannot be blank", HttpStatus.BAD_REQUEST),

    // Authentication and Authorization
    UNAUTHENTICATED(5001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(5002, "You do not have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_INACTIVE(5003, "Your Account is not Active", HttpStatus.UNAUTHORIZED), // Sửa lỗi cú pháp và thêm mã lỗi này


    // CATEGORY
    CATEGORY_TITLE_NOTBLANK(6001, "Title of category cannot be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_DESCRIPTION_NOTBLANK(6002, "Description of category cannot be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_IS_REQUIRED(6003, "Category is required", HttpStatus.BAD_REQUEST),
    SUB_CATEGORY_TITLE_NOTBLANK(6004, "Title of sub category cannot be blank", HttpStatus.BAD_REQUEST),
    SUB_CATEGORY_DESCRIPTION_NOTBLANK(6005, "Description of sub category cannot be blank", HttpStatus.BAD_REQUEST),
    //    CATEGORY_ID_NOTBLANK(6006, "Category ID cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_ID(6006, "Category ID is invalid or missing", HttpStatus.BAD_REQUEST),

    CATEGORY_NOTFOUND(6007, "Category not found", HttpStatus.NOT_FOUND),
    SUB_CATEGORY_NOTFOUND(6008, "Sub category not found", HttpStatus.NOT_FOUND),

    TOKEN_NOTBLANK(7001, "Token cannot be blank", HttpStatus.BAD_REQUEST),

    FILE_SIZE_EXCEEDED(8001, "Max file size is 2MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(8002, "Only jpg, png, gif, bmp files are allowed", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(8003, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),

    DATA_INITIALIZATION_FAILED(9001, "Data initialization failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private int code;
    private String message;
    private HttpStatus httpStatusCode;
}
