package tomas.aguirrezabala.gestion_academica.persistence.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.persistence.AlumnoDao;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;

@Repository
public class AlumnoDaoMemoryImpl implements AlumnoDao {
    
    private final Map<Long, Alumno> alumnos = new HashMap<>();
    private final AtomicLong ultimoId = new AtomicLong(0);

    @Autowired
    @Lazy
    private AsignaturaDao asignaturaDao;
    
    @Override
    public Alumno guardar(Alumno alumno) {
        if (alumno.getId() == null) {
            alumno.setId(ultimoId.incrementAndGet());
        }
        alumnos.put(alumno.getId(), alumno);
        return alumno;
    }
    
    @Override
    public Optional<Alumno> buscarPorId(Long alumnoId) {
        Alumno alumno = alumnos.get(alumnoId);
        
        if (alumno != null) {

            Alumno alumnoCopia = clonarAlumno(alumno);
            
            if (asignaturaDao != null) {
                List<Asignatura> asignaturas = asignaturaDao.buscarPorAlumnoId(alumnoId);
                alumnoCopia.setAsignaturas(asignaturas);
            }
            
            return Optional.of(alumnoCopia);
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Alumno> buscarTodos() {
        List<Alumno> resultado = new ArrayList<>();
        
        for (Alumno alumno : alumnos.values()) {
            Alumno alumnoCopia = clonarAlumno(alumno);
            
            if (asignaturaDao != null) {
                List<Asignatura> asignaturas = asignaturaDao.buscarPorAlumnoId(alumno.getId());
                alumnoCopia.setAsignaturas(asignaturas);
            }
            
            resultado.add(alumnoCopia);
        }
        
        return resultado;
    }
    
    @Override
    public void borrarPorId(Long alumnoId) {
        alumnos.remove(alumnoId);
    }
    
    private Alumno clonarAlumno(Alumno original) {
        Alumno clon = new Alumno();
        clon.setId(original.getId());
        clon.setNombre(original.getNombre());
        clon.setApellido(original.getApellido());
        clon.setDni(original.getDni());
        clon.setCarrera(original.getCarrera());
        
        return clon;
    }
}