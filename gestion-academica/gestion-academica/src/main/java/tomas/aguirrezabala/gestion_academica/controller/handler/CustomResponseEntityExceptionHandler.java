package tomas.aguirrezabala.gestion_academica.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<CustomApiError> manejarEntidadNoEncontrada(
            EntidadNoEncontradaException ex, WebRequest request) {
        
        CustomApiError apiError = new CustomApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<CustomApiError> manejarReglaNegocio(
            ReglaNegocioException ex, WebRequest request) {
        
        CustomApiError apiError = new CustomApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EntidadDuplicadaException.class)
    public ResponseEntity<CustomApiError> manejarEntidadDuplicada(
            EntidadDuplicadaException ex, WebRequest request) {
        
        CustomApiError apiError = new CustomApiError(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiError> manejarExcepcionGeneral(
            Exception ex, WebRequest request) {
        
        CustomApiError apiError = new CustomApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Error interno del servidor: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}