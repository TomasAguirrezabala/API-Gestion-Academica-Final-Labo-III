package tomas.aguirrezabala.gestion_academica.business.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tomas.aguirrezabala.gestion_academica.business.AlumnoService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AlumnoDto;
import tomas.aguirrezabala.gestion_academica.persistence.AlumnoDao;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    @Autowired
    private AlumnoDao alumnoDao;
    
    @Autowired
    private MateriaDao materiaDao;
    
    @Autowired
    private AsignaturaDao asignaturaDao;

    @Override
    public Alumno guardar(AlumnoDto alumnoDto) throws EntidadDuplicadaException {
        if (alumnoDto.getId() == null && alumnoDto.getDni() != null) {
            boolean existeAlumnoConMismoDni = alumnoDao.buscarTodos().stream()
                    .anyMatch(a -> alumnoDto.getDni().equals(a.getDni()));
            
            if (existeAlumnoConMismoDni) {
                throw new EntidadDuplicadaException("Alumno", "DNI", alumnoDto.getDni());
            }
        }
        
        Alumno alumno = new Alumno();
        alumno.setId(alumnoDto.getId());
        alumno.setNombre(alumnoDto.getNombre());
        alumno.setApellido(alumnoDto.getApellido());
        alumno.setDni(alumnoDto.getDni());
        
        return alumnoDao.guardar(alumno);
    }

    @Override
    public Optional<Alumno> buscarPorId(Long alumnoId) {
        return alumnoDao.buscarPorId(alumnoId);
    }

    @Override
    public List<Alumno> buscarTodos() {
        return alumnoDao.buscarTodos();
    }

    @Override
    public void eliminarPorId(Long alumnoId) throws EntidadNoEncontradaException, ReglaNegocioException {

        Optional<Alumno> alumnoOptional = alumnoDao.buscarPorId(alumnoId);
        if (alumnoOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Alumno", alumnoId);
        }
        
        List<Asignatura> asignaturas = asignaturaDao.buscarPorAlumnoId(alumnoId);
        if (asignaturas != null && !asignaturas.isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el alumno porque tiene asignaturas asociadas");
        }
        
        alumnoDao.borrarPorId(alumnoId);
    }

    @Override
    public Asignatura inscribirEnMateria(Long alumnoId, Long materiaId)
            throws EntidadNoEncontradaException, EntidadDuplicadaException, ReglaNegocioException {
       
        Optional<Alumno> alumnoOptional = alumnoDao.buscarPorId(alumnoId);
        if (alumnoOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Alumno", alumnoId);
        }
        Alumno alumno = alumnoOptional.get();
              
        Optional<Materia> materiaOptional = materiaDao.buscarPorId(materiaId);
        if (materiaOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Materia", materiaId);
        }
        Materia materia = materiaOptional.get();
        
        Optional<Asignatura> asignaturaExistente = asignaturaDao.buscarPorAlumnoIdYMateriaId(alumnoId, materiaId);
        if (asignaturaExistente.isPresent()) {
            throw new EntidadDuplicadaException("El alumno ya está inscrito en esta materia");
        }

        verificarCorrelatividades(alumno, materia);
        
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        return asignaturaDao.guardar(asignatura);
    }

    private void verificarCorrelatividades(Alumno alumno, Materia materia) {

        List<Long> correlatividades = materia.getCorrelatividades();
        if (correlatividades == null || correlatividades.isEmpty()) {
            return;
        }

        List<Asignatura> asignaturasDelAlumno = asignaturaDao.buscarPorAlumnoId(alumno.getId());

        for (Long correlativaId : correlatividades) {
            boolean cumpleCorrelativa = asignaturasDelAlumno.stream()
                .anyMatch(a -> correlativaId.equals(a.getMateria().getId()) && 
                          (a.getEstado() == EstadoAsignatura.APROBADO || 
                           a.getEstado() == EstadoAsignatura.REGULAR));
            
            if (!cumpleCorrelativa) {

                String nombreCorrelativa = materiaDao.buscarPorId(correlativaId)
                    .map(Materia::getNombre)
                    .orElse("ID: " + correlativaId);
                
                throw new ReglaNegocioException(
                    "No se puede inscribir en " + materia.getNombre() + 
                    " porque no cumple con la correlatividad " + nombreCorrelativa);
            }
        }
    }

    @Override
    public Asignatura cambiarEstadoAsignatura(Long alumnoId, Long asignaturaId, EstadoAsignatura nuevoEstado)
            throws EntidadNoEncontradaException, ReglaNegocioException {
      
        Optional<Alumno> alumnoOptional = alumnoDao.buscarPorId(alumnoId);
        if (alumnoOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Alumno", alumnoId);
        }
        
        Optional<Asignatura> asignaturaOptional = asignaturaDao.buscarPorId(asignaturaId);
        if (asignaturaOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Asignatura", asignaturaId);
        }
        Asignatura asignatura = asignaturaOptional.get();
        
        if (!alumnoId.equals(asignatura.getAlumno().getId())) {
            throw new ReglaNegocioException("La asignatura no pertenece al alumno especificado");
        }
        
        asignatura.setEstado(nuevoEstado);
        
        return asignaturaDao.guardar(asignatura);
    }

    @Override
    public List<Asignatura> obtenerAsignaturas(Long alumnoId) throws EntidadNoEncontradaException {
      
        Optional<Alumno> alumnoOptional = alumnoDao.buscarPorId(alumnoId);
        if (alumnoOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Alumno", alumnoId);
        }
        
        return asignaturaDao.buscarPorAlumnoId(alumnoId);
    }
}