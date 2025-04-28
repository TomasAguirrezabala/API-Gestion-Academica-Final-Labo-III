package tomas.aguirrezabala.gestion_academica.model;

import java.util.ArrayList;
import java.util.List;

public class Profesor {
    private Long id;
    private String nombre;
    private String apellido;
    private String titulo;
    private List<Materia> materias = new ArrayList<>();
    
    public Profesor() {
    }
    
    public Profesor(Long id, String nombre, String apellido, String titulo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.titulo = titulo;
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
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public List<Materia> getMaterias() {
        return materias;
    }
    
    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}