package tomas.aguirrezabala.gestion_academica.model.dto;

import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;

public class AsignaturaDto {
    private Long id;
    private Long materiaId;
    private Long alumnoId;
    private EstadoAsignatura estado;
    private Double nota;
    
    
    public AsignaturaDto() {
    }
    
    public AsignaturaDto(Long id, Long materiaId, Long alumnoId, EstadoAsignatura estado, Double nota) {
        this.id = id;
        this.materiaId = materiaId;
        this.alumnoId = alumnoId;
        this.estado = estado;
        this.nota = nota;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getMateriaId() {
        return materiaId;
    }
    
    public void setMateriaId(Long materiaId) {
        this.materiaId = materiaId;
    }
    
    public Long getAlumnoId() {
        return alumnoId;
    }
    
    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }
    
    public EstadoAsignatura getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoAsignatura estado) {
        this.estado = estado;
    }
    
    public Double getNota() {
        return nota;
    }
    
    public void setNota(Double nota) {
        this.nota = nota;
    }
}