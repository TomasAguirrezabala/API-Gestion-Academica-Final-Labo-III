package tomas.aguirrezabala.gestion_academica.model;

import java.util.ArrayList;
import java.util.List;

public class Carrera {
    private Long id;
    private String nombre;
    private Integer duracionAnios;
    private List<Materia> materias = new ArrayList<>();
    
    // Default constructor
    public Carrera() {
    }
    
    // Parameterized constructor
    public Carrera(Long id, String nombre, Integer duracionAnios) {
        this.id = id;
        this.nombre = nombre;
        this.duracionAnios = duracionAnios;
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
    
    public Integer getDuracionAnios() {
        return duracionAnios;
    }
    
    public void setDuracionAnios(Integer duracionAnios) {
        this.duracionAnios = duracionAnios;
    }
    
    public List<Materia> getMaterias() {
        return materias;
    }
    
    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}