package com.system.invoicesystem.exception.handler;

import com.system.invoicesystem.constant.StatusCode;
import com.system.invoicesystem.dto.ErrorResponseDto;
import com.system.invoicesystem.exception.InvalidPaymentException;
import com.system.invoicesystem.exception.InvoiceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleInvoiceNotFoundException(InvoiceNotFoundException ex) {
        return ResponseEntity.status(StatusCode.INVOICE_NOT_FOUND).body(new ErrorResponseDto(LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPaymentException(InvalidPaymentException ex) {
        return ResponseEntity.status(StatusCode.INVALID_PAYMENT).body(new ErrorResponseDto(LocalDateTime.now(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream().map((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            return String.format("%s : %s", fieldName, errorMessage);
        }).toList();

        String errorMessage = errors.stream().findFirst().orElse("Invalid argument");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(LocalDateTime.now(), errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        return ResponseEntity.status(StatusCode.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(LocalDateTime.now(), ex.getMessage()));
    }

}
