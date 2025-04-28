package tomas.aguirrezabala.gestion_academica.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;


@Repository
public class MateriaDaoMemoryImpl implements MateriaDao {
    
    private final Map<Long, Materia> materias = new HashMap<>();
    private final AtomicLong ultimoId = new AtomicLong(0);
    
    @Override
    public Materia guardar(Materia materia) {
        if (materia.getId() == null) {
            materia.setId(ultimoId.incrementAndGet());
        }
        materias.put(materia.getId(), materia);
        return materia;
    }
    
    @Override
    public Optional<Materia> buscarPorId(Long materiaId) {
        return Optional.ofNullable(materias.get(materiaId));
    }
    
    @Override
    public List<Materia> buscarAll() {
        return new ArrayList<>(materias.values());
    }
    
    @Override
    public void borrarPorId(Long materiaId) {
        materias.remove(materiaId);
    }
}