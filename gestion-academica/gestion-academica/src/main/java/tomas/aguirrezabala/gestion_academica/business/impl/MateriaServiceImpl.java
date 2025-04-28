package tomas.aguirrezabala.gestion_academica.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tomas.aguirrezabala.gestion_academica.business.MateriaService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.MateriaDto;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;
import tomas.aguirrezabala.gestion_academica.persistence.ProfesorDao;

@Service
public class MateriaServiceImpl implements MateriaService {

    @Autowired
    private MateriaDao materiaDao;
    
    @Autowired
    private ProfesorDao profesorDao;
    
    @Autowired
    private AsignaturaDao asignaturaDao;

    @Override
    public Materia guardar(MateriaDto materiaDto) throws EntidadDuplicadaException, EntidadNoEncontradaException {
        if (materiaDto.getId() == null && materiaDto.getNombre() != null) {
            boolean existeMateriaConMismoNombre = materiaDao.buscarAll().stream()
                    .anyMatch(m -> materiaDto.getNombre().equals(m.getNombre()));
            
            if (existeMateriaConMismoNombre) {
                throw new EntidadDuplicadaException("Materia", "nombre", materiaDto.getNombre());
            }
        }
        Profesor profesor = null;
        if (materiaDto.getProfesorId() != null) {
            Optional<Profesor> profesorOptional = profesorDao.buscarPorId(materiaDto.getProfesorId());
            if (profesorOptional.isEmpty()) {
                throw new EntidadNoEncontradaException("Profesor", materiaDto.getProfesorId());
            }
            profesor = profesorOptional.get();
        }
        
        Materia materia = new Materia();
        materia.setId(materiaDto.getId());
        materia.setNombre(materiaDto.getNombre());
        materia.setAnio(materiaDto.getAnio());
        materia.setCuatrimestre(materiaDto.getCuatrimestre());
        materia.setProfesor(profesor);
        
        if (materiaDto.getCorrelatividades() != null && !materiaDto.getCorrelatividades().isEmpty()) {
            List<Long> correlatividades = new ArrayList<>(materiaDto.getCorrelatividades());
            for (Long correlativaId : correlatividades) {
                if (!materiaDao.buscarPorId(correlativaId).isPresent()) {
                    throw new EntidadNoEncontradaException("Materia correlativa", correlativaId);
                }
            }
            materia.setCorrelatividades(correlatividades);
        }
        
        return materiaDao.guardar(materia);
    }

    @Override
    public Optional<Materia> buscarPorId(Long materiaId) {
        return materiaDao.buscarPorId(materiaId);
    }

    @Override
    public List<Materia> buscarTodas() {
        return materiaDao.buscarAll();
    }

    @Override
    public void eliminarPorId(Long materiaId) throws EntidadNoEncontradaException, ReglaNegocioException {
        Optional<Materia> materiaOptional = materiaDao.buscarPorId(materiaId);
        if (materiaOptional.isEmpty()) {
            throw new EntidadNoEncontradaException("Materia", materiaId);
        }
        
        List<Materia> materiasQueUsanComoCorrelativa = materiaDao.buscarAll().stream()
                .filter(m -> m.getCorrelatividades().contains(materiaId))
                .collect(Collectors.toList());
        
        if (!materiasQueUsanComoCorrelativa.isEmpty()) {
            String materiasDependientes = materiasQueUsanComoCorrelativa.stream()
                    .map(Materia::getNombre)
                    .collect(Collectors.joining(", "));
            throw new ReglaNegocioException("No se puede eliminar la materia porque es correlativa de: " + materiasDependientes);
        }
        
        if (asignaturaDao.existePorMateriaId(materiaId)) {
            throw new ReglaNegocioException("No se puede eliminar la materia porque tiene alumnos inscriptos");
        }
        
        materiaDao.borrarPorId(materiaId);
    }

    @Override
    public Materia crearConCorrelatividades(Materia materia, List<Long> correlatividades) 
            throws EntidadNoEncontradaException, ReglaNegocioException {
        
        if (correlatividades != null && !correlatividades.isEmpty()) {
            for (Long correlativaId : correlatividades) {
                Optional<Materia> correlativaOptional = materiaDao.buscarPorId(correlativaId);
                if (correlativaOptional.isEmpty()) {
                    throw new EntidadNoEncontradaException("Materia correlativa", correlativaId);
                }
                
                if (creariaUnCicloDeCorrelatividades(correlativaId, materia.getId(), new ArrayList<>())) {
                    throw new ReglaNegocioException("La correlatividad crearía un ciclo, lo cual no está permitido");
                }
            }
            
            materia.setCorrelatividades(correlatividades);
        }
        
        return materiaDao.guardar(materia);
    }
    

    private boolean creariaUnCicloDeCorrelatividades(Long materiaId, Long correlativaId, List<Long> visitadas) {
        if (visitadas.contains(materiaId)) {
            return true;
        }
        
        if (materiaId.equals(correlativaId)) {
            return true;
        }

        visitadas.add(materiaId);

        Optional<Materia> materiaOptional = materiaDao.buscarPorId(materiaId);
        if (materiaOptional.isEmpty()) {
            return false; 
        }

        Materia materia = materiaOptional.get();
        for (Long materiaCorrelativaId : materia.getCorrelatividades()) {
            if (creariaUnCicloDeCorrelatividades(materiaCorrelativaId, correlativaId, new ArrayList<>(visitadas))) {
                return true;
            }
        }
        
        return false;
    }
}