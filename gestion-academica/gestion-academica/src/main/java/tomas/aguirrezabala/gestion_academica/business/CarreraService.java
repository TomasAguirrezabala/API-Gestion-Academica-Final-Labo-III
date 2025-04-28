package tomas.aguirrezabala.gestion_academica.business;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.dto.CarreraDto;

public interface CarreraService {
    
    
    Carrera guardar(CarreraDto carrera) throws EntidadDuplicadaException;
    
    Optional<Carrera> buscarPorId(Long id);
    
    List<Carrera> buscarTodas();
    
    void eliminarPorId(Long id) throws EntidadNoEncontradaException;
    
    Carrera agregarMateria(Long carreraId, Long materiaId)
    throws EntidadNoEncontradaException, EntidadDuplicadaException;
    
}