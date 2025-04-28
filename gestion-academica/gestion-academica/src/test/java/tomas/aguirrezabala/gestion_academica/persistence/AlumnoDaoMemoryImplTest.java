package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.test.util.ReflectionTestUtils;

import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.persistence.impl.AlumnoDaoMemoryImpl;

public class AlumnoDaoMemoryImplTest {
    
    private AlumnoDaoMemoryImpl alumnoDao;
    private AsignaturaDao asignaturaDaoMock;
    private Carrera carrera;
    
    @BeforeEach
    void setUp() {
        alumnoDao = new AlumnoDaoMemoryImpl();
        carrera = new Carrera(1L, "Técnico Universitario en Programación", 2);
        
        // Crear mock de AsignaturaDao
        asignaturaDaoMock = mock(AsignaturaDao.class);
        
        // Inyectar el mock en el dao
        ReflectionTestUtils.setField(alumnoDao, "asignaturaDao", asignaturaDaoMock);
    }
    
    @Test
    void guardar_debeAsignarId_cuandoAlumnoNuevo() {
        // Preparación
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setDni("12345678");
        alumno.setCarrera(carrera);
        
        // Ejecución
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        
        // Verificación
        assertNotNull(alumnoGuardado.getId());
        assertEquals("Tomas", alumnoGuardado.getNombre());
        assertEquals("Aguirrezabala", alumnoGuardado.getApellido());
    }
    
    @Test
    void guardar_debeActualizar_cuandoAlumnoExistente() {
        // Preparación
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();
        
        // Configurar mock para devolver lista vacía de asignaturas
        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(new ArrayList<>());
        
        // Actualización
        alumnoGuardado.setNombre("Tomas Actualizado");
        alumnoDao.guardar(alumnoGuardado);
        
        // Verificación
        Optional<Alumno> recuperado = alumnoDao.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals("Tomas Actualizado", recuperado.get().getNombre());
    }
    
    @Test
    void buscarPorId_debeRetornarAlumno_cuandoExisteId() {
        // Preparación
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();
        
        // Configurar mock para devolver lista vacía de asignaturas
        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(new ArrayList<>());
        
        // Ejecución
        Optional<Alumno> resultado = alumnoDao.buscarPorId(id);
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("Tomas", resultado.get().getNombre());
        assertEquals("Aguirrezabala", resultado.get().getApellido());
        
        // Verificar que se llamó al método del AsignaturaDao
        verify(asignaturaDaoMock).buscarPorAlumnoId(id);
    }
    
    @Test
    void buscarPorId_debeCargarAsignaturas_cuandoExisteId() {
        // Preparación
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();
        
        // Crear asignaturas de prueba
        Materia materia = new Materia(1L, "Programación I", 1, 1);
        
        List<Asignatura> asignaturasSimuladas = Arrays.asList(
            new Asignatura(1L, materia, alumnoGuardado, EstadoAsignatura.CURSANDO, null),
            new Asignatura(2L, materia, alumnoGuardado, EstadoAsignatura.REGULAR, 8.0)
        );
        
        // Configurar mock para devolver las asignaturas simuladas
        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(asignaturasSimuladas);
        
        // Ejecución
        Optional<Alumno> resultado = alumnoDao.buscarPorId(id);
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getAsignaturas().size());
        
        // Verificar que se llamó al método del AsignaturaDao
        verify(asignaturaDaoMock).buscarPorAlumnoId(id);
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {
        // Ejecución y verificación
        assertFalse(alumnoDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarTodos_debeRetornarListaVacia_cuandoNoHayAlumnos() {
        // Ejecución y verificación
        assertTrue(alumnoDao.buscarTodos().isEmpty());
    }
    
    @Test
    void buscarTodos_debeRetornarTodosLosAlumnos_cuandoHayAlumnos() {
        // Preparación
        Alumno alumno1 = new Alumno();
        alumno1.setNombre("Tomas");
        alumno1.setApellido("Aguirrezabala");
        alumno1.setCarrera(carrera);
        
        Alumno alumno2 = new Alumno();
        alumno2.setNombre("Juan");
        alumno2.setApellido("Pérez");
        alumno2.setCarrera(carrera);
        
        Alumno alumnoGuardado1 = alumnoDao.guardar(alumno1);
        Alumno alumnoGuardado2 = alumnoDao.guardar(alumno2);
        
        // Configurar mock para devolver lista vacía de asignaturas
        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado1.getId())).thenReturn(new ArrayList<>());
        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado2.getId())).thenReturn(new ArrayList<>());
        
        // Ejecución
        List<Alumno> alumnos = alumnoDao.buscarTodos();
        
        // Verificación
        assertEquals(2, alumnos.size());
        
        // Verificar que se llamó al método del AsignaturaDao para cada alumno
        verify(asignaturaDaoMock).buscarPorAlumnoId(alumnoGuardado1.getId());
        verify(asignaturaDaoMock).buscarPorAlumnoId(alumnoGuardado2.getId());
    }
    
    @Test
    void borrarPorId_debeEliminarAlumno_cuandoExisteId() {
        // Preparación
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();
        
        // Configurar mock para devolver lista vacía de asignaturas
        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(new ArrayList<>());
        
        // Verificar que existe antes de borrar
        assertTrue(alumnoDao.buscarPorId(id).isPresent());
        
        // Ejecución
        alumnoDao.borrarPorId(id);
        
        // Verificación
        assertFalse(alumnoDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void borrarPorId_noDebeHacerNada_cuandoIdNoExiste() {
        // Preparación - Guardar un alumno
        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        
        // Configurar mock para devolver lista vacía de asignaturas
        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado.getId())).thenReturn(new ArrayList<>());
        
        int cantidadAntes = alumnoDao.buscarTodos().size();
        
        // Ejecución
        alumnoDao.borrarPorId(999L);
        
        // Verificación
        assertEquals(cantidadAntes, alumnoDao.buscarTodos().size());
    }
}