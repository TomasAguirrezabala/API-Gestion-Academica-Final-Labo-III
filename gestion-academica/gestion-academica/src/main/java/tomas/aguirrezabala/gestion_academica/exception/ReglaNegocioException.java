package tomas.aguirrezabala.gestion_academica.exception;

public class ReglaNegocioException extends RuntimeException {
    
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}