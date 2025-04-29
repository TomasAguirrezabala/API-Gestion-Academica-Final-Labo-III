package tomas.aguirrezabala.gestion_academica.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ProfesorDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String titulo;
    private List<Long> materiasIds = new ArrayList<>();
    
   
    public ProfesorDto() {
    }
    
    public ProfesorDto(Long id, String nombre, String apellido, String titulo, List<Long> materiasIds) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.titulo = titulo;
        this.materiasIds = materiasIds;
    }

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
    
    public List<Long> getMateriasIds() {
        return materiasIds;
    }
    
    public void setMateriasIds(List<Long> materiasIds) {
        this.materiasIds = materiasIds;
    }
}