package tomas.aguirrezabala.gestion_academica.business;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.dto.AsignaturaDto;

public interface AsignaturaService {
    
    Asignatura guardar(AsignaturaDto asignatura)
    throws EntidadNoEncontradaException, EntidadDuplicadaException;
    
    Optional<Asignatura> buscarPorId(Long asignaturaId);
    
    List<Asignatura> buscarTodas();
    
    void eliminarPorId(Long asignaturaId) throws EntidadNoEncontradaException;

}