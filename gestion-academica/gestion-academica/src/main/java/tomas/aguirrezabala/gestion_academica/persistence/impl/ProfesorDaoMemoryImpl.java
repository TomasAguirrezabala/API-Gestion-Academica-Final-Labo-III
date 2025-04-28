package tomas.aguirrezabala.gestion_academica.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.persistence.ProfesorDao;

@Repository
public class ProfesorDaoMemoryImpl implements ProfesorDao {
    
    private final Map<Long, Profesor> profesores = new HashMap<>();
    private final AtomicLong ultimoId = new AtomicLong(0);
    
    @Override
    public Profesor guardar(Profesor profesor) {
        if (profesor.getId() == null) {
            profesor.setId( ultimoId.incrementAndGet());
        }
        profesores.put(profesor.getId(), profesor);
        return profesor;
    }
    
    @Override
    public Optional<Profesor> buscarPorId(Long profesorId) {
        return Optional.ofNullable(profesores.get(profesorId));
    }
    
    @Override
    public List<Profesor> buscarAll() {
        return new ArrayList<>(profesores.values());
    }
    
    @Override
    public void borrarPorId(Long profesorId) {
        profesores.remove(profesorId);
    }
    
}