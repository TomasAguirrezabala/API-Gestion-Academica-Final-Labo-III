package tomas.aguirrezabala.gestion_academica.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class Alumno {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private List<Asignatura> asignaturas = new ArrayList<>();
    private Carrera carrera;
    
    public Alumno() {
    }
    
    public Alumno(Long id, String nombre, String apellido, String dni, Carrera carrera) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.carrera = carrera;
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
    
    public List<Asignatura> getAsignaturas() {
        return asignaturas;
    }
    
    public void setAsignaturas(List<Asignatura> asignaturas) {
        this.asignaturas = asignaturas;
    }
    
    public Carrera getCarrera() {
        return carrera;
    }
    
    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }
}