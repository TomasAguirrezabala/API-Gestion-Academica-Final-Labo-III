package tomas.aguirrezabala.gestion_academica.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;

@Repository
public class AsignaturaDaoMemoryImpl implements AsignaturaDao {
    
    private final Map<Long, Asignatura> asignaturas = new HashMap<>();
    private final AtomicLong ultimoId = new AtomicLong(0);
    
    @Override
    public Asignatura guardar(Asignatura asignatura) {
        if (asignatura.getId() == null) {
            asignatura.setId(ultimoId.incrementAndGet());
        }
        asignaturas.put(asignatura.getId(), asignatura);
        return asignatura;
    }
    
    @Override
    public Optional<Asignatura> buscarPorId(Long asignaturaId) {
        return Optional.ofNullable(asignaturas.get(asignaturaId));
    }
    
    @Override
    public List<Asignatura> buscarTodos() {
        return new ArrayList<>(asignaturas.values());
    }
    
    @Override
    public void borrarPorId(Long asignaturaId) {
        asignaturas.remove(asignaturaId);
    }
    
    @Override
    public List<Asignatura> buscarPorAlumnoId(Long alumnoId) {
        return buscarTodos().stream()
                .filter(asignatura -> asignatura.getAlumno() != null && 
                        alumnoId.equals(asignatura.getAlumno().getId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Asignatura> buscarPorAlumnoIdYMateriaId(Long alumnoId, Long materiaId) {
        return buscarTodos().stream()
                .filter(asignatura -> asignatura.getAlumno() != null && 
                        alumnoId.equals(asignatura.getAlumno().getId()) &&
                        asignatura.getMateria() != null &&
                        materiaId.equals(asignatura.getMateria().getId()))
                .findFirst();
    }
    @Override
    public boolean existePorMateriaId(Long materiaId) {
        return buscarTodos().stream()
                .anyMatch(asignatura -> asignatura.getMateria() != null && 
                        materiaId.equals(asignatura.getMateria().getId()));
    }
}