package tomas.aguirrezabala.gestion_academica.exception;

public class EntidadNoEncontradaException extends RuntimeException {
    
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
    
    public EntidadNoEncontradaException(String entidad, Long id) {
        super(String.format("%s con id %d no encontrado/a", entidad, id));
    }
}