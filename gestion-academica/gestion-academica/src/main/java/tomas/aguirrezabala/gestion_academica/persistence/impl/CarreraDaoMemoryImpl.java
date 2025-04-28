package tomas.aguirrezabala.gestion_academica.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.persistence.CarreraDao;

@Repository
public class CarreraDaoMemoryImpl implements CarreraDao {
    
    private final Map<Long, Carrera> carreras = new HashMap<>();
    private final AtomicLong ultimoId = new AtomicLong(0);
    
    @Override
    public Carrera guardar(Carrera carrera) {
        if (carrera.getId() == null) {
            carrera.setId(ultimoId.incrementAndGet());
        }
        carreras.put(carrera.getId(), carrera);
        return carrera;
    }
    
    @Override
    public Optional<Carrera> buscarPorId(Long carreraId) {
        return Optional.ofNullable(carreras.get(carreraId));
    }
    
    @Override
    public List<Carrera> buscarAll() {
        return new ArrayList<>(carreras.values());
    }
    
    @Override
    public void borrarPorId(Long carreraId) {
        carreras.remove(carreraId);
    }

}