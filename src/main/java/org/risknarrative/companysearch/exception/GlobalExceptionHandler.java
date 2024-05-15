package org.risknarrative.companysearch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionResponseDto> handleApplicationException(ApplicationException ex) {
        ExceptionResponseDto exceptionResponseDTO = new ExceptionResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exceptionResponseDTO);
    }

    @ExceptionHandler(ApplicationUserException.class)
    public ResponseEntity<ExceptionResponseDto> handleApplicationUserException(ApplicationUserException ex) {
        ExceptionResponseDto exceptionResponseDTO = new ExceptionResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponseDTO);
    }

}