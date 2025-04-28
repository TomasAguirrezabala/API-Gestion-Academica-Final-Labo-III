package tomas.aguirrezabala.gestion_academica.business;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.MateriaDto;

public interface MateriaService {
    
    Materia guardar(MateriaDto materia) throws EntidadDuplicadaException, EntidadNoEncontradaException;
    
    Optional<Materia> buscarPorId(Long materiaId);
    
    List<Materia> buscarTodas();
    
    void eliminarPorId(Long materiaId) throws EntidadNoEncontradaException, ReglaNegocioException;
    
    Materia crearConCorrelatividades(Materia materia, List<Long> correlatividades)
    throws EntidadNoEncontradaException, ReglaNegocioException;
    
    
    
    
}