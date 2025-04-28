package tomas.aguirrezabala.gestion_academica.business.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tomas.aguirrezabala.gestion_academica.business.AsignaturaService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AsignaturaDto;
import tomas.aguirrezabala.gestion_academica.persistence.AlumnoDao;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

@Service
public class AsignaturaServiceImpl implements AsignaturaService {

    @Autowired
    private AsignaturaDao asignaturaDao;
    
    @Autowired
    private AlumnoDao alumnoDao;
    
    @Autowired
    private MateriaDao materiaDao;
    
    @Override
    public Asignatura guardar(AsignaturaDto asignaturaDto)
            throws EntidadNoEncontradaException, EntidadDuplicadaException {

        Alumno alumno = null;
        if (asignaturaDto.getAlumnoId() != null) {
            Optional<Alumno> alumnoOpt = alumnoDao.buscarPorId(asignaturaDto.getAlumnoId());
            if (alumnoOpt.isEmpty()) {
                throw new EntidadNoEncontradaException("Alumno", asignaturaDto.getAlumnoId());
            }
            alumno = alumnoOpt.get();
        } else {
            throw new EntidadNoEncontradaException("Alumno", null);
        }

        Materia materia = null;
        if (asignaturaDto.getMateriaId() != null) {
            Optional<Materia> materiaOpt = materiaDao.buscarPorId(asignaturaDto.getMateriaId());
            if (materiaOpt.isEmpty()) {
                throw new EntidadNoEncontradaException("Materia", asignaturaDto.getMateriaId());
            }
            materia = materiaOpt.get();
        } else {
            throw new EntidadNoEncontradaException("Materia", null);
        }

        if (asignaturaDto.getId() == null) {
            Optional<Asignatura> asignaturaExistente = asignaturaDao.buscarPorAlumnoIdYMateriaId(
                    asignaturaDto.getAlumnoId(), asignaturaDto.getMateriaId());
            if (asignaturaExistente.isPresent()) {
                throw new EntidadDuplicadaException("Asignatura", "alumno y materia", 
                        "Alumno ID: " + asignaturaDto.getAlumnoId() + 
                        ", Materia ID: " + asignaturaDto.getMateriaId());
            }
            
            verificarCorrelatividades(alumno, materia);
        }

        if (asignaturaDto.getId() != null) {
            Optional<Asignatura> asignaturaOpt = asignaturaDao.buscarPorId(asignaturaDto.getId());
            if (asignaturaOpt.isEmpty()) {
                throw new EntidadNoEncontradaException("Asignatura", asignaturaDto.getId());
            }
        }

        Asignatura asignatura = new Asignatura();
        asignatura.setId(asignaturaDto.getId());
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia);
        asignatura.setEstado(asignaturaDto.getEstado());
        asignatura.setNota(asignaturaDto.getNota());
        
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
    public Optional<Asignatura> buscarPorId(Long asignaturaId) {
        return asignaturaDao.buscarPorId(asignaturaId);
    }

    @Override
    public List<Asignatura> buscarTodas() {
        return asignaturaDao.buscarTodos();
    }

    @Override
    public void eliminarPorId(Long asignaturaId) throws EntidadNoEncontradaException {
        Optional<Asignatura> asignaturaOpt = asignaturaDao.buscarPorId(asignaturaId);
        if (asignaturaOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Asignatura", asignaturaId);
        }
        
        asignaturaDao.borrarPorId(asignaturaId);
    }
}