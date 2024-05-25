package com.andreidodu.europealibrary.resource.advice;

import com.andreidodu.europealibrary.dto.HttpErrorDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.exception.EntityNotFoundException;
import com.andreidodu.europealibrary.exception.ValidationException;
import com.andreidodu.europealibrary.exception.WorkInProgressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public HttpErrorDTO handleEntityNotFoundException(EntityNotFoundException e) {
        return new HttpErrorDTO(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = {WorkInProgressException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public HttpErrorDTO handleWorkInProgressException(WorkInProgressException e) {
        return new HttpErrorDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage());
    }


    @ExceptionHandler(value = {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public HttpErrorDTO handleBadCredentialsException(BadCredentialsException e) {
        return new HttpErrorDTO(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HttpErrorDTO handleValidationException(ValidationException e) {
        return new HttpErrorDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = {ApplicationException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpErrorDTO handleApplicationExceptionException(ApplicationException e) {
        return new HttpErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(value = {NullPointerException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public HttpErrorDTO handleApplicationExceptionException(NullPointerException e) {
        log.debug(e.toString());
        return new HttpErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    HttpErrorDTO onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        HttpErrorDTO error = new HttpErrorDTO();
        error.setCode(400);
        error.setMessage("validations errors: ");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.setMessage(error.getMessage() + " " + fieldError.getField() + "-" + fieldError.getDefaultMessage());
        }
        return error;
    }

}