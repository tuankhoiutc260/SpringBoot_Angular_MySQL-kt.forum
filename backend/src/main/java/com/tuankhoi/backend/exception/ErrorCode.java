package com.tuankhoi.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // General errors (1xxx)
    UNCATEGORIZED_EXCEPTION(1000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_VIOLATION(1001, "Data integrity violation", HttpStatus.BAD_REQUEST),
    DATABASE_CONSTRAINT_VIOLATION(1002, "Database constraint violation", HttpStatus.BAD_REQUEST),
    UNKNOWN_ERROR(1003, "An unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INITIALIZATION_FAILED(1004, "Data initialization failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication and Authorization errors (2xxx)
    UNAUTHENTICATED(2001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(2002, "You do not have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_INACTIVE(2003, "Your Account is not Active", HttpStatus.UNAUTHORIZED),
    TOKEN_NOTBLANK(2004, "Token cannot be blank", HttpStatus.BAD_REQUEST),

    // User related errors (3xxx)
    USER_EMAIL_INVALID(3001, "Email should be valid", HttpStatus.BAD_REQUEST),
    USER_EMAIL_NOTBLANK(3002, "Email cannot be null", HttpStatus.BAD_REQUEST),
    USER_NAME_NOTBLANK(3003, "Username cannot be null", HttpStatus.BAD_REQUEST),
    USER_NAME_INVALID(3004, "Username must be at least 6 characters", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_NOTBLANK(3005, "Password cannot be null", HttpStatus.BAD_REQUEST),
    USER_PASSWORD_INVALID(3006, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    USER_EXISTED(3007, "User existed", HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTED(3008, "Email existed", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(3009, "User not found", HttpStatus.NOT_FOUND),
    USER_USERNAME_PASSWORD_INVALID(3010, "Incorrect Username or Password", HttpStatus.UNAUTHORIZED),

    // Role related errors (4xxx)
    ROLE_EXISTED(4001, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOTFOUND(4002, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_NOTNULL(4003, "Role ID cannot be null", HttpStatus.BAD_REQUEST),
    ROLE_NAME_NOTBLANK(4004, "Role name cannot be blank", HttpStatus.BAD_REQUEST),

    // Permission related errors (5xxx)
    PERMISSION_EXISTED(5001, "Permission existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOTFOUND(5002, "Permission not found", HttpStatus.NOT_FOUND),
    PERMISSION_NAME_NOTBLANK(5003, "Permission cannot be blank", HttpStatus.BAD_REQUEST),
    PERMISSION_DESCRIPTION_NOTBLANK(5004, "Permission description cannot be blank", HttpStatus.BAD_REQUEST),

    // Category related errors (6xxx)
    CATEGORY_TITLE_NOTBLANK(6001, "Title of category cannot be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_DESCRIPTION_NOTBLANK(6002, "Description of category cannot be blank", HttpStatus.BAD_REQUEST),
    CATEGORY_IS_REQUIRED(6003, "Category is required", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_ID(6004, "Category ID is invalid or missing", HttpStatus.BAD_REQUEST),
    CATEGORY_NOTFOUND(6005, "Category not found", HttpStatus.NOT_FOUND),

    // Sub-category related errors (7xxx)
    SUB_CATEGORY_TITLE_NOTBLANK(7001, "Title of sub category cannot be blank", HttpStatus.BAD_REQUEST),
    SUB_CATEGORY_DESCRIPTION_NOTBLANK(7002, "Description of sub category cannot be blank", HttpStatus.BAD_REQUEST),
    INVALID_SUB_CATEGORY_ID(7003, "SubCategory ID is invalid or missing", HttpStatus.BAD_REQUEST),
    SUB_CATEGORY_NOTFOUND(7004, "Sub category not found", HttpStatus.NOT_FOUND),

    // Post related errors (8xxx)
    POST_TITLE_NOTBLANK(8001, "Title cannot be blank", HttpStatus.BAD_REQUEST),
    POST_TITLE_INVALID(8002, "Title cannot be longer than 255 characters", HttpStatus.BAD_REQUEST),
    POST_CONTENT_NOTBLANK(8003, "Content cannot be blank", HttpStatus.BAD_REQUEST),
    POST_TAGS_NOTEMPTY(8004, "Tags cannot be empty", HttpStatus.BAD_REQUEST),
    POST_NOTFOUND(8005, "Post not found", HttpStatus.NOT_FOUND),
    POST_ID_NOTBLANK(8006, "Post ID cannot be blank", HttpStatus.BAD_REQUEST),

    // Comment related errors (9xxx)
    COMMENT_CONTENT_NOTBLANK(9001, "Comment content cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_POST_ID_NOTBLANK(9002, "Post ID of Comment cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_PARENT_ID_NOTBLANK(9003, "Comment Parent ID cannot be blank", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND(9004, "Comment not found", HttpStatus.NOT_FOUND),
    PARENT_COMMENT_NOTFOUND(9005, "Parent Comment not found", HttpStatus.NOT_FOUND),

    // File related errors (10xxx)
    FILE_SIZE_EXCEEDED(10001, "Max file size is 2MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(10002, "Only jpg, png, gif, bmp files are allowed", HttpStatus.BAD_REQUEST),
    UPLOAD_FAILED(10003, "Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETE_FAILED(10004, "Delete file in Cloudinary failed", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_PROCESSING_ERROR(10005, "Error processing file", HttpStatus.INTERNAL_SERVER_ERROR),

    // Elasticsearch related errors (11xxx)
    ELASTICSEARCH_INDEXING_ERROR(11001, "Failed to index in Elasticsearch", HttpStatus.INTERNAL_SERVER_ERROR),

    // Pagination related errors (12xxx)
    INVALID_PAGINATION_PARAMETERS(12001, "Invalid pagination parameters", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus httpStatusCode;
}