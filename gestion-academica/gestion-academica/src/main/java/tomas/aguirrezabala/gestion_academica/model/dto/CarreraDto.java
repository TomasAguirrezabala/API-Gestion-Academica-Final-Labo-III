package tomas.aguirrezabala.gestion_academica.model.dto;

import java.util.ArrayList;
import java.util.List;

public class CarreraDto {
    private Long id;
    private String nombre;
    private Integer cantidadCuatrimestres;
    private List<Long> materiasIds = new ArrayList<>();
    
    
    public CarreraDto() {
    }
    
    public CarreraDto(Long id, String nombre, Integer cantidadCuatrimestres, List<Long> materiasIds) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadCuatrimestres = cantidadCuatrimestres;
        this.materiasIds = materiasIds;
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
    
    public Integer getCantidadCuatrimestres() {
        return cantidadCuatrimestres;
    }
    
    public void setDuracionAnios(Integer cantidadCuatrimestres) {
        this.cantidadCuatrimestres = cantidadCuatrimestres;
    }
    
    public List<Long> getMateriasIds() {
        return materiasIds;
    }
    
    public void setMateriasIds(List<Long> materiasIds) {
        this.materiasIds = materiasIds;
    }
}