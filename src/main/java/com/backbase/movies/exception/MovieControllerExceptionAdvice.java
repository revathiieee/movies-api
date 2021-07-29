package com.backbase.movies.exception;

import com.backbase.movies.controller.MoviesController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Handles the exception thrown from Movies Controlle
 *
 * @author Revathir
 */
@RestControllerAdvice(assignableTypes = {MoviesController.class})
public class MovieControllerExceptionAdvice {
    /**
     * Receives MovieHttpException
     * @param ex MovieHttpException
     * @return response Message with http status
     */
    @ExceptionHandler({MovieHttpException.class})
    public ResponseEntity<String> handleMovieHttpException(MovieHttpException ex) {

        HttpStatus status = HttpStatus.resolve(ex.getHttpStatus());
        if(status == null) status = INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}
