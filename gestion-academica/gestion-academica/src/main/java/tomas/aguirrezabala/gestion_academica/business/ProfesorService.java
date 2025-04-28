package tomas.aguirrezabala.gestion_academica.business;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.ProfesorDto;

public interface ProfesorService {
    
    Profesor guardar(ProfesorDto profesor) throws EntidadDuplicadaException;
    
    Optional<Profesor> buscarPorId(Long id);
    
    List<Profesor> buscarTodos();
    
    void eliminarPorId(Long id) throws EntidadNoEncontradaException, ReglaNegocioException;
    
}