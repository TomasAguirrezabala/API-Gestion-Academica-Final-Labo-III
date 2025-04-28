package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.model.Materia;

public interface MateriaDao {
    Materia guardar(Materia materia);
    Optional<Materia> buscarPorId(Long materiaId);
    List<Materia> buscarAll();
    void borrarPorId(Long materiaId);
}