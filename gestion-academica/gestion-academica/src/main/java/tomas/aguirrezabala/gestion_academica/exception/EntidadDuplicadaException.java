package tomas.aguirrezabala.gestion_academica.exception;

public class EntidadDuplicadaException extends RuntimeException {
    
    public EntidadDuplicadaException(String mensaje) {
        super(mensaje);
    }
    
    public EntidadDuplicadaException(String entidad, String campo, String valor) {
        super(String.format("Ya existe %s con %s: %s", entidad, campo, valor));
    }
}