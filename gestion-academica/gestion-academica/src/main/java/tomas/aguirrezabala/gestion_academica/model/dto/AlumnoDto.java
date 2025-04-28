package tomas.aguirrezabala.gestion_academica.model.dto;

import java.util.ArrayList;
import java.util.List;

public class AlumnoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private Long carreraId;
    private List<Long> asignaturasIds = new ArrayList<>();
    
    // Default constructor
    public AlumnoDto() {
    }
    
    public AlumnoDto(Long id, String nombre, String apellido, String dni, Long carreraId, List<Long> asignaturasIds) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.carreraId = carreraId;
        this.asignaturasIds = asignaturasIds;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public Long getCarreraId() {
        return carreraId;
    }
    
    public void setCarreraId(Long carreraId) {
        this.carreraId = carreraId;
    }
    
    public List<Long> getAsignaturasIds() {
        return asignaturasIds;
    }
    
    public void setAsignaturasIds(List<Long> asignaturasIds) {
        this.asignaturasIds = asignaturasIds;
    }
}