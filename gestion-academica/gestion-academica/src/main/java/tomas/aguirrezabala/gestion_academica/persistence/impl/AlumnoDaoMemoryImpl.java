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
    
    // Uso @Lazy para evitar una dependencia circular entre AlumnoDao y AsignaturaDao
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
            // Crear una copia del alumno para no modificar el original en el mapa
            Alumno alumnoCopia = clonarAlumno(alumno);
            
            // Cargar las asignaturas asociadas a este alumno
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
        
        // Por cada alumno, cargar sus asignaturas
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
    
    // Método auxiliar para clonar un alumno
    private Alumno clonarAlumno(Alumno original) {
        Alumno clon = new Alumno();
        clon.setId(original.getId());
        clon.setNombre(original.getNombre());
        clon.setApellido(original.getApellido());
        clon.setDni(original.getDni());
        clon.setCarrera(original.getCarrera());
        
        // No copiamos las asignaturas porque las cargaremos desde el DAO
        return clon;
    }
}