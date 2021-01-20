package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Method to handle SignUpRestrictedException if the user already exists in the database during signup
     *
     * @param excp      - SignUpRestrictedException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictionException(
            SignUpRestrictedException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Method to handle AuthenticationFailedException if incorrect username/ password are provided during user signin
     *
     * @param excp      - AuthenticationFailedException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(
            AuthenticationFailedException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to handle SignOutRestrictedException if incorrect authorization token is provided during user sign out
     *
     * @param excp      - SignOutRestrictedException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(
            SignOutRestrictedException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to handle AuthorizationFailedException if incorrect authorization token is provided during user sign out
     *
     * @param excp      - AuthorizationFailedException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(
            AuthorizationFailedException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Method to handle UserNotFoundException if user does not exist in the database
     *
     * @param excp      - UserNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(
            UserNotFoundException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle InvalidQuestionException if user does not exist in the database
     *
     * @param excp      - InvalidQuestionException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(
            InvalidQuestionException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle AnswerNotFoundException if user does not exist in the database
     *
     * @param excp      - AnswerNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(
            AnswerNotFoundException excp, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
}
