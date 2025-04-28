package tomas.aguirrezabala.gestion_academica.business;

import java.util.List;
import java.util.Optional;

import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.dto.AlumnoDto;

public interface AlumnoService {

    Alumno guardar(AlumnoDto alumno) throws EntidadDuplicadaException;
    
    Optional<Alumno> buscarPorId(Long alumnoId);

    List<Alumno> buscarTodos();
   
    void eliminarPorId(Long alumnoId)
    throws EntidadNoEncontradaException, ReglaNegocioException;
  
    Asignatura inscribirEnMateria(Long alumnoId, Long materiaId)
    throws EntidadNoEncontradaException, EntidadDuplicadaException, ReglaNegocioException;
    
    Asignatura cambiarEstadoAsignatura(Long alumnoId, Long asignaturaId, EstadoAsignatura nuevoEstado)
    throws EntidadNoEncontradaException, ReglaNegocioException;;
    
    List<Asignatura> obtenerAsignaturas(Long alumnoId) throws EntidadNoEncontradaException;
}