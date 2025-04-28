package tomas.aguirrezabala.gestion_academica.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

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
        
        // Crear objetos comunes para los tests
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
        // Preparación - Guardar una asignatura
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();
        
        // Actualización
        asignaturaGuardada.setEstado(EstadoAsignatura.REGULAR);
        asignaturaGuardada.setNota(8.0);
        asignaturaDao.guardar(asignaturaGuardada);
        
        // Verificación
        Optional<Asignatura> recuperada = asignaturaDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals(EstadoAsignatura.REGULAR, recuperada.get().getEstado());
        assertEquals(8.0, recuperada.get().getNota());
    }
    
    @Test
    void buscarPorId_debeRetornarAsignatura_cuandoExisteId() {
        // Preparación
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();
        
        // Ejecución
        Optional<Asignatura> resultado = asignaturaDao.buscarPorId(id);
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals(alumno, resultado.get().getAlumno());
        assertEquals(materia1, resultado.get().getMateria());
        assertEquals(EstadoAsignatura.CURSANDO, resultado.get().getEstado());
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {
        // Ejecución y verificación
        assertFalse(asignaturaDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarTodos_debeRetornarListaVacia_cuandoNoHayAsignaturas() {
        // Ejecución y verificación
        assertTrue(asignaturaDao.buscarTodos().isEmpty());
    }
    
    @Test
    void buscarTodos_debeRetornarTodasLasAsignaturas_cuandoHayAsignaturas() {
        // Preparación
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
        
        // Ejecución
        List<Asignatura> asignaturas = asignaturaDao.buscarTodos();
        
        // Verificación
        assertEquals(2, asignaturas.size());
    }
    
    @Test
    void borrarPorId_debeEliminarAsignatura_cuandoExisteId() {
        // Preparación
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaGuardada = asignaturaDao.guardar(asignatura);
        Long id = asignaturaGuardada.getId();
        
        // Verificar que existe antes de borrar
        assertTrue(asignaturaDao.buscarPorId(id).isPresent());
        
        // Ejecución
        asignaturaDao.borrarPorId(id);
        
        // Verificación
        assertFalse(asignaturaDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void buscarPorAlumnoId_debeRetornarAsignaturas_cuandoAlumnoTieneAsignaturas() {
        // Preparación
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
        
        // Ejecución
        List<Asignatura> asignaturas = asignaturaDao.buscarPorAlumnoId(alumno.getId());
        
        // Verificación
        assertEquals(2, asignaturas.size());
    }
    
    @Test
    void buscarPorAlumnoId_debeRetornarListaVacia_cuandoAlumnoNoTieneAsignaturas() {
        // Ejecución y verificación
        assertTrue(asignaturaDao.buscarPorAlumnoId(999L).isEmpty());
    }
    
    @Test
    void buscarPorAlumnoIdYMateriaId_debeRetornarAsignatura_cuandoExiste() {
        // Preparación
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        asignaturaDao.guardar(asignatura);
        
        // Ejecución
        Optional<Asignatura> resultado = asignaturaDao.buscarPorAlumnoIdYMateriaId(alumno.getId(), materia1.getId());
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals(alumno, resultado.get().getAlumno());
        assertEquals(materia1, resultado.get().getMateria());
    }
    
    @Test
    void buscarPorAlumnoIdYMateriaId_debeRetornarOptionalVacio_cuandoNoExiste() {
        // Ejecución y verificación
        assertFalse(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumno.getId(), 999L).isPresent());
    }
    
    @Test
    void existePorMateriaId_debeRetornarTrue_cuandoExistenAsignaturasConMateria() {
        // Preparación
        Asignatura asignatura = new Asignatura();
        asignatura.setAlumno(alumno);
        asignatura.setMateria(materia1);
        asignatura.setEstado(EstadoAsignatura.CURSANDO);
        
        asignaturaDao.guardar(asignatura);
        
        // Ejecución y verificación
        assertTrue(asignaturaDao.existePorMateriaId(materia1.getId()));
    }
    
    @Test
    void existePorMateriaId_debeRetornarFalse_cuandoNoExistenAsignaturasConMateria() {
        // Ejecución y verificación
        assertFalse(asignaturaDao.existePorMateriaId(999L));
    }
}