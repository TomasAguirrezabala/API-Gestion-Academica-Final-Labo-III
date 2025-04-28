package tomas.aguirrezabala.gestion_academica.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class Asignatura {
    private Long id;
    private Materia materia;
    private Alumno alumno;
    private EstadoAsignatura estado;
    private Double nota;

    public Asignatura() {
    }

    public Asignatura(Long id, Materia materia, Alumno alumno, EstadoAsignatura estado) {
        this.id = id;
        this.materia = materia;
        this.alumno = alumno;
        this.estado = estado;
    }

    public Asignatura(Long id, Materia materia, Alumno alumno, EstadoAsignatura estado, Double nota) {
        this.id = id;
        this.materia = materia;
        this.alumno = alumno;
        this.estado = estado;
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Materia getMateria() {
        return materia;
    }
    
    public void setMateria(Materia materia) {
        this.materia = materia;
    }
    
    public Alumno getAlumno() {
        return alumno;
    }
    
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
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