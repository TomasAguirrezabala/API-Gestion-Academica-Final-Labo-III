package tomas.aguirrezabala.gestion_academica.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tomas.aguirrezabala.gestion_academica.business.CarreraService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.CarreraDto;
import tomas.aguirrezabala.gestion_academica.persistence.CarreraDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

@Service
public class CarreraServiceImpl implements CarreraService {
    
    @Autowired
    private CarreraDao carreraDao;
    
    @Autowired
    private MateriaDao materiaDao;
    
    @Override
    public Carrera guardar(CarreraDto carreraDto) throws EntidadDuplicadaException {
        if (carreraDto.getId() == null && carreraDto.getNombre() != null) {
            boolean existeCarreraConMismoNombre = carreraDao.buscarAll().stream()
                    .anyMatch(c -> carreraDto.getNombre().equals(c.getNombre()));
            
            if (existeCarreraConMismoNombre) {
                throw new EntidadDuplicadaException("Carrera", "nombre", carreraDto.getNombre());
            }
        }

        Carrera carrera = new Carrera();
        carrera.setId(carreraDto.getId());
        carrera.setNombre(carreraDto.getNombre());
        carrera.setDuracionAnios(carreraDto.getCantidadCuatrimestres());

        if (carreraDto.getMateriasIds() != null && !carreraDto.getMateriasIds().isEmpty()) {
            List<Materia> materias = new ArrayList<>();
            for (Long materiaId : carreraDto.getMateriasIds()) {
                Optional<Materia> materiaOpt = materiaDao.buscarPorId(materiaId);
                materiaOpt.ifPresent(materias::add);
            }
            carrera.setMaterias(materias);
        }
        
        return carreraDao.guardar(carrera);
    }
    
    @Override
    public Optional<Carrera> buscarPorId(Long id) {
        return carreraDao.buscarPorId(id);
    }
    
    @Override
    public List<Carrera> buscarTodas() {
        return carreraDao.buscarAll();
    }
    
    @Override
    public void eliminarPorId(Long id) throws EntidadNoEncontradaException {
        Optional<Carrera> carreraOpt = carreraDao.buscarPorId(id);
        if (carreraOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Carrera", id);
        }
        
        carreraDao.borrarPorId(id);
    }
    
    @Override
    public Carrera agregarMateria(Long carreraId, Long materiaId) 
            throws EntidadNoEncontradaException, EntidadDuplicadaException {

        Optional<Carrera> carreraOpt = carreraDao.buscarPorId(carreraId);
        if (carreraOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Carrera", carreraId);
        }

        Optional<Materia> materiaOpt = materiaDao.buscarPorId(materiaId);
        if (materiaOpt.isEmpty()) {
            throw new EntidadNoEncontradaException("Materia", materiaId);
        }
        
        Carrera carrera = carreraOpt.get();
        Materia materia = materiaOpt.get();

        boolean materiaYaAsignada = carrera.getMaterias().stream()
                .anyMatch(m -> m.getId().equals(materiaId));
                
        if (materiaYaAsignada) {
            throw new EntidadDuplicadaException("Materia", "id", materiaId.toString() + 
                    " ya está asignada a la carrera " + carrera.getNombre());
        }

        carrera.getMaterias().add(materia);

        return carreraDao.guardar(carrera);
    }
}