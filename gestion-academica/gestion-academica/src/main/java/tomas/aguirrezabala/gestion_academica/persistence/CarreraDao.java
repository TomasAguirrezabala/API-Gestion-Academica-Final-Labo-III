package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.model.Carrera;

public interface CarreraDao {
    Carrera guardar(Carrera carrera);
    Optional<Carrera> buscarPorId(Long carreraId);
    List<Carrera> buscarAll();
    void borrarPorId(Long carreraId);
}