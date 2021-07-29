package com.backbase.movies.exception;

import lombok.Generated;

/**
 * This is the Custom Exception class
 *
 * @author Revathi
 */
public class MovieHttpException extends RuntimeException {
    /**
     * field httpStatus
     */
    private final int httpStatus;

    /**
     * Constuctor Instatiation with exception Message and Httpstatus
     *
     * @param message    exception Message
     * @param httpStatus 401/404/500
     */
    private MovieHttpException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Forms the Internal server error exception
     *
     * @param message exception Message
     * @return exception
     */
    public static MovieHttpException serverError(String message) {
        return new MovieHttpException(message, 500);
    }

    /**
     * Forms the Bad Request exception
     *
     * @param message exception Message
     * @return exception
     */
    public static MovieHttpException badRequest(String message) {
        return new MovieHttpException(message, 400);
    }

    /**
     * Forms the Unauthorised Exception
     *
     * @param message exception Message
     * @return exception
     */
    public static MovieHttpException unAuthorized(String message) {
        return new MovieHttpException(message, 401);
    }

    /**
     * Forms the Not Found Exception
     *
     * @param message exception Message
     * @return exception
     */
    public static MovieHttpException notFound(String message) {
        return new MovieHttpException(message, 404);
    }

    @Generated
    public int getHttpStatus() {
        return this.httpStatus;
    }
}
