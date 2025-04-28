package tomas.aguirrezabala.gestion_academica.business.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tomas.aguirrezabala.gestion_academica.business.ProfesorService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.ProfesorDto;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;
import tomas.aguirrezabala.gestion_academica.persistence.ProfesorDao;

@Service
public class ProfesorServiceImpl implements ProfesorService {

    @Autowired
    private ProfesorDao profesorDao;
    
    @Autowired
    private MateriaDao materiaDao;
    
    @Override
    public Profesor guardar(ProfesorDto profesorDto) throws EntidadDuplicadaException {
        if (profesorDto.getId() == null && profesorDto.getNombre() != null && profesorDto.getApellido() != null) {
            boolean existeProfesorConMismoNombreYApellido = profesorDao.buscarAll().stream()
                    .anyMatch(p -> profesorDto.getNombre().equals(p.getNombre()) && 
                                   profesorDto.getApellido().equals(p.getApellido()));
            
            if (existeProfesorConMismoNombreYApellido) {
                throw new EntidadDuplicadaException("Profesor", "nombre y apellido", 
                        profesorDto.getNombre() + " " + profesorDto.getApellido());
            }
        }

        Profesor profesor = new Profesor();
        profesor.setId(profesorDto.getId());
        profesor.setNombre(profesorDto.getNombre());
        profesor.setApellido(profesorDto.getApellido());
        profesor.setTitulo(profesorDto.getTitulo());

        if (profesorDto.getMateriasIds() != null && !profesorDto.getMateriasIds().isEmpty()) {
            List<Materia> materias = new ArrayList<>();
            for (Long materiaId : profesorDto.getMateriasIds()) {
                Optional<Materia> materiaOpt = materiaDao.buscarPorId(materiaId);
                materiaOpt.ifPresent(materias::add);
            }
            profesor.setMaterias(materias);
        }
        
        return profesorDao.guardar(profesor);
    }

    @Override
    public Optional<Profesor> buscarPorId(Long id) {
        return profesorDao.buscarPorId(id);
    }

    @Override
    public List<Profesor> buscarTodos() {
        return profesorDao.buscarAll();
    }

    @Override
    public void eliminarPorId(Long id) throws EntidadNoEncontradaException, ReglaNegocioException {
        Optional<Profesor> profesorOpt = profesorDao.buscarPorId(id);
        if (profesorOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Profesor", id);
        }
        
        Profesor profesor = profesorOpt.get();

        if (profesor.getMaterias() != null && !profesor.getMaterias().isEmpty()) {
            throw new ReglaNegocioException("No se puede eliminar el profesor porque tiene materias asignadas");
        }

        List<Materia> materiasDelProfesor = materiaDao.buscarAll().stream()
                .filter(m -> m.getProfesor() != null && id.equals(m.getProfesor().getId()))
                .collect(Collectors.toList());
        
        if (!materiasDelProfesor.isEmpty()) {
            String nombresMaterias = materiasDelProfesor.stream()
                    .map(Materia::getNombre)
                    .collect(Collectors.joining(", "));
            throw new ReglaNegocioException(
                    "No se puede eliminar el profesor porque dicta las siguientes materias: " + nombresMaterias);
        }
        
        profesorDao.borrarPorId(id);
    }

    public List<Materia> obtenerMateriasOrdenadas(Long profesorId) throws EntidadNoEncontradaException {
        Optional<Profesor> profesorOpt = profesorDao.buscarPorId(profesorId);
        if (profesorOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Profesor", profesorId);
        }

        List<Materia> materiasDelProfesor = new ArrayList<>(profesorOpt.get().getMaterias());
        
        List<Materia> materiasDeBD = materiaDao.buscarAll().stream()
                .filter(m -> m.getProfesor() != null && profesorId.equals(m.getProfesor().getId()))
                .collect(Collectors.toList());

        for (Materia materia : materiasDeBD) {
            if (!materiasDelProfesor.contains(materia)) {
                materiasDelProfesor.add(materia);
            }
        }

        materiasDelProfesor.sort(Comparator.comparing(Materia::getNombre));
        
        return materiasDelProfesor;
    }
      
}