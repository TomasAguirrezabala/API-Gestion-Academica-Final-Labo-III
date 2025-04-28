package tomas.aguirrezabala.gestion_academica.controller.handler;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CustomApiError {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    
    private int status;
    private String error;
    private String mensaje;
    private String path;
    
    public CustomApiError() {
        this.timestamp = LocalDateTime.now();
    }
    
    public CustomApiError(int status, String error, String mensaje, String path) {
        this();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.path = path;
    }
    
    // Getters y setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}