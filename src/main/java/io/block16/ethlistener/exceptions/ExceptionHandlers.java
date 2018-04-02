package io.block16.ethlistener.exceptions;

import io.block16.ethlistener.dto.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public GenericResponse<Void> handleUserNotFoundException(final NotFoundException ex) {
        return new GenericResponse<>(ex.getMessage(), true, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public GenericResponse<Void> handleThrowable(final BadRequestException ex) {
        return new GenericResponse<>(ex.getMessage(), true, null);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    @ResponseBody
    public GenericResponse<Void> handleThrowable(final ConflictException ex) {
        return new GenericResponse<>(ex.getMessage(), true, null);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NotImplementedException.class)
    @ResponseBody
    public GenericResponse<Void> handleThrowable(final NotImplementedException ex) {
        return new GenericResponse<>(ex.getMessage(), true, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GenericResponse<List<FieldError>> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        return new GenericResponse<>(ex.getMessage(), true, result.getFieldErrors());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({InternalServerException.class, UpstreamException.class})
    @ResponseBody
    public GenericResponse<Void> handleThrowable(final RuntimeException ex) {
        return new GenericResponse<>(ex.getMessage(), true, null);
    }

    // TODO: Generic case isn't actually handled here lol. Throwable -> 500
}
