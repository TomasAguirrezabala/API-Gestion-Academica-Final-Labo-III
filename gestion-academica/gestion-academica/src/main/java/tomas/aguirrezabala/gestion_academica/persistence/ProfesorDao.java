package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.model.Profesor;

public interface ProfesorDao {
    Profesor guardar(Profesor profesor);
    Optional<Profesor> buscarPorId(Long profesorId);
    List<Profesor> buscarAll();
    void borrarPorId(Long profesorId);
}