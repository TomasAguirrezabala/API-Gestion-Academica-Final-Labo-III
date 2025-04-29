package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.persistence.impl.AsignaturaDaoMemoryImpl;

public class AsignaturaDaoMemoryImplTest {
    
    private AsignaturaDaoMemoryImpl asignaturaDao;
    private Carrera carrera;
    private Alumno alumno;
    private Materia materia1, materia2;
    
    @BeforeEach
    void setUp() {
        asignaturaDao = new AsignaturaDaoMemoryImpl();

        carrera = new Carrera(1L, "Técnico Universitario en Programación", 2);
        
        alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        materia1.setAnio(1);
        materia1.setCuatrimestre(1);
        
        materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        materia2.setAnio(1);
        materia2.setCuatrimestre(2);
    }
    
    @Test
    void guardar_debeAsignarId_cuandoAsignaturaNueva() {
        // Preparación
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        // Ejecución
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        
        // Verificación
        assertNotNull(asignaturaGuardada.getId());
        assertEquals(alumno, asignaturaGuardada.getAlumno());
        assertEquals(materia1, asignaturaGuardada.getMateria());
        assertEquals(EstadoAsignatura.CURSANDO, asignaturaGuardada.getEstado());
    }
    
    @Test
    void guardar_debeActualizar_cuandoAsignaturaExistente() {

        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();

        asignaturaGuardada.setEstado(EstadoAsignatura.REGULAR);
        asignaturaGuardada.setNota(8.0);
        asignaturaDao.guardar(asignaturaGuardada);

        Optional<Asignatura> recuperada = asignaturaDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals(EstadoAsignatura.REGULAR, recuperada.get().getEstado());
        assertEquals(8.0, recuperada.get().getNota());
    }
    
    @Test
    void buscarPorId_debeRetornarAsignatura_cuandoExisteId() {

        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();

        Optional<Asignatura> resultado = asignaturaDao.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(alumno, resultado.get().getAlumno());
        assertEquals(materia1, resultado.get().getMateria());
        assertEquals(EstadoAsignatura.CURSANDO, resultado.get().getEstado());
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {
        assertFalse(asignaturaDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarTodos_debeRetornarListaVacia_cuandoNoHayAsignaturas() {
        assertTrue(asignaturaDao.buscarTodos().isEmpty());
    }
    
    @Test
    void buscarTodos_debeRetornarTodasLasAsignaturas_cuandoHayAsignaturas() {

        Asignatura asignatura1 = new Asignatura();
        asignatura1.setAlumno(alumno);
        asignatura1.setMateria(materia1);
        asignatura1.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignatura2 = new Asignatura();
        asignatura2.setAlumno(alumno);
        asignatura2.setMateria(materia2);
        asignatura2.setEstado(EstadoAsignatura.REGULAR);
        
        asignaturaDao.guardar(asignatura1);
        asignaturaDao.guardar(asignatura2);

        List<Asignatura> asignaturas = asignaturaDao.buscarTodos();

        assertEquals(2, asignaturas.size());
    }
    
    @Test
    void borrarPorId_debeEliminarAsignatura_cuandoExisteId() {

        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();

        assertTrue(asignaturaDao.buscarPorId(id).isPresent());

        asignaturaDao.borrarPorId(id);

        assertFalse(asignaturaDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void buscarPorAlumnoId_debeRetornarAsignaturas_cuandoAlumnoTieneAsignaturas() {

        Asignatura asignatura1 = new Asignatura();
        asignatura1.setAlumno(alumno);
        asignatura1.setMateria(materia1);
        asignatura1.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignatura2 = new Asignatura();
        asignatura2.setAlumno(alumno);
        asignatura2.setMateria(materia2);
        asignatura2.setEstado(EstadoAsignatura.REGULAR);
        
        asignaturaDao.guardar(asignatura1);
        asignaturaDao.guardar(asignatura2);

        List<Asignatura> asignaturas = asignaturaDao.buscarPorAlumnoId(alumno.getId());

        assertEquals(2, asignaturas.size());
    }
    
    @Test
    void buscarPorAlumnoId_debeRetornarListaVacia_cuandoAlumnoNoTieneAsignaturas() {
        assertTrue(asignaturaDao.buscarPorAlumnoId(999L).isEmpty());
    }
    
    @Test
    void buscarPorAlumnoIdYMateriaId_debeRetornarAsignatura_cuandoExiste() {

        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        asignaturaDao.guardar(asignatura);

        Optional<Asignatura> resultado = asignaturaDao.buscarPorAlumnoIdYMateriaId(alumno.getId(), materia1.getId());

        assertTrue(resultado.isPresent());
        assertEquals(alumno, resultado.get().getAlumno());
        assertEquals(materia1, resultado.get().getMateria());
    }
    
    @Test
    void buscarPorAlumnoIdYMateriaId_debeRetornarOptionalVacio_cuandoNoExiste() {

        assertFalse(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumno.getId(), 999L).isPresent());
    }
    
    @Test
    void existePorMateriaId_debeRetornarTrue_cuandoExistenAsignaturasConMateria() {

        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        asignaturaDao.guardar(asignatura);

        assertTrue(asignaturaDao.existePorMateriaId(materia1.getId()));
    }
    
    @Test
    void existePorMateriaId_debeRetornarFalse_cuandoNoExistenAsignaturasConMateria() {
        assertFalse(asignaturaDao.existePorMateriaId(999L));
    }
}