package tomas.aguirrezabala.gestion_academica.model;

import java.util.ArrayList;
import java.util.List;

public class Materia {
    private Long id;
    private String nombre;
    private Integer anio;
    private Integer cuatrimestre;
    private Profesor profesor;
    private List<Long> correlatividades = new ArrayList<>();

    public Materia() {
    }

    public Materia(Long id, String nombre, Integer anio, Integer cuatrimestre) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.cuatrimestre = cuatrimestre;
    }

    public Materia(Long id, String nombre, Integer anio, Integer cuatrimestre, Profesor profesor) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.cuatrimestre = cuatrimestre;
        this.profesor = profesor;
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
    
    public Profesor getProfesor() {
        return profesor;
    }
    
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }
    
    public List<Long> getCorrelatividades() {
        return correlatividades;
    }
    
    public void setCorrelatividades(List<Long> correlatividades) {
        this.correlatividades = correlatividades;
    }
}