package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.model.Asignatura;

public interface AsignaturaDao {
    Asignatura guardar(Asignatura asignatura);
    Optional<Asignatura> buscarPorId(Long asignaturaid);
    List<Asignatura> buscarTodos();
    void borrarPorId(Long asignaturaid);
    List<Asignatura> buscarPorAlumnoId(Long alumnoId);
    Optional<Asignatura> buscarPorAlumnoIdYMateriaId(Long alumnoId, Long materiaId);
    boolean existePorMateriaId(Long materiaId);
}