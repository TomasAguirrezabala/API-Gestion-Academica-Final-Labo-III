package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.model.Alumno;

public interface AlumnoDao {
    Alumno guardar(Alumno alumno);
    Optional<Alumno> buscarPorId(Long alumnoId);
    List<Alumno> buscarTodos();
    void borrarPorId(Long alumnoId);
}