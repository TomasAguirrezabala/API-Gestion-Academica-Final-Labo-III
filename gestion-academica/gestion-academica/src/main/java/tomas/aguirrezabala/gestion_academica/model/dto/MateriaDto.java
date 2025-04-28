package tomas.aguirrezabala.gestion_academica.model.dto;

import java.util.ArrayList;
import java.util.List;

public class MateriaDto {
    private Long id;
    private String nombre;
    private Integer anio;
    private Integer cuatrimestre;
    private Long profesorId;
    private List<Long> correlatividades = new ArrayList<>();
    
    public MateriaDto() {
    }
    
    public MateriaDto(Long id, String nombre, Integer anio, Integer cuatrimestre, Long profesorId, List<Long> correlatividades) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.cuatrimestre = cuatrimestre;
        this.profesorId = profesorId;
        this.correlatividades = correlatividades;
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
    
    public Integer getAnio() {
        return anio;
    }
    
    public void setAnio(Integer anio) {
        this.anio = anio;
    }
    
    public Integer getCuatrimestre() {
        return cuatrimestre;
    }
    
    public void setCuatrimestre(Integer cuatrimestre) {
        this.cuatrimestre = cuatrimestre;
    }
    
    public Long getProfesorId() {
        return profesorId;
    }
    
    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }
    
    public List<Long> getCorrelatividades() {
        return correlatividades;
    }
    
    public void setCorrelatividades(List<Long> correlatividades) {
        this.correlatividades = correlatividades;
    }
}