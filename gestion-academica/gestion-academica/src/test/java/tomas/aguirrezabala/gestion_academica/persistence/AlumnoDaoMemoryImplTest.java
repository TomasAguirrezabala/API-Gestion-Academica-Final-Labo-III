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

        asignaturaDaoMock = mock(AsignaturaDao.class);

        ReflectionTestUtils.setField(alumnoDao, "asignaturaDao", asignaturaDaoMock);
    }
    
    @Test
    void guardar_debeAsignarId_cuandoAlumnoNuevo() {

        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setDni("12345678");
        alumno.setCarrera(carrera);

        Alumno alumnoGuardado = alumnoDao.guardar(alumno);

        assertNotNull(alumnoGuardado.getId());
        assertEquals("Tomas", alumnoGuardado.getNombre());
        assertEquals("Aguirrezabala", alumnoGuardado.getApellido());
    }
    
    @Test
    void guardar_debeActualizar_cuandoAlumnoExistente() {

        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();

        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(new ArrayList<>());

        alumnoGuardado.setNombre("Tomas Actualizado");
        alumnoDao.guardar(alumnoGuardado);

        Optional<Alumno> recuperado = alumnoDao.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals("Tomas Actualizado", recuperado.get().getNombre());
    }
    
    @Test
    void buscarPorId_debeRetornarAlumno_cuandoExisteId() {

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

        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();

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

        verify(asignaturaDaoMock).buscarPorAlumnoId(id);
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {

        assertFalse(alumnoDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarTodos_debeRetornarListaVacia_cuandoNoHayAlumnos() {

        assertTrue(alumnoDao.buscarTodos().isEmpty());
    }
    
    @Test
    void buscarTodos_debeRetornarTodosLosAlumnos_cuandoHayAlumnos() {

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

        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado1.getId())).thenReturn(new ArrayList<>());
        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado2.getId())).thenReturn(new ArrayList<>());

        List<Alumno> alumnos = alumnoDao.buscarTodos();
        
        assertEquals(2, alumnos.size());

        verify(asignaturaDaoMock).buscarPorAlumnoId(alumnoGuardado1.getId());
        verify(asignaturaDaoMock).buscarPorAlumnoId(alumnoGuardado2.getId());
    }
    
    @Test
    void borrarPorId_debeEliminarAlumno_cuandoExisteId() {

        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);
        Long id = alumnoGuardado.getId();

        when(asignaturaDaoMock.buscarPorAlumnoId(id)).thenReturn(new ArrayList<>());

        assertTrue(alumnoDao.buscarPorId(id).isPresent());

        alumnoDao.borrarPorId(id);

        assertFalse(alumnoDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void borrarPorId_noDebeHacerNada_cuandoIdNoExiste() {

        Alumno alumno = new Alumno();
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Alumno alumnoGuardado = alumnoDao.guardar(alumno);

        when(asignaturaDaoMock.buscarPorAlumnoId(alumnoGuardado.getId())).thenReturn(new ArrayList<>());
        
        int cantidadAntes = alumnoDao.buscarTodos().size();

        alumnoDao.borrarPorId(999L);

        assertEquals(cantidadAntes, alumnoDao.buscarTodos().size());
    }
}