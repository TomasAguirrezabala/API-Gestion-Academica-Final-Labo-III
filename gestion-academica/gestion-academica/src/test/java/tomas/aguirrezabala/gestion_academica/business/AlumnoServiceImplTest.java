package tomas.aguirrezabala.gestion_academica.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tomas.aguirrezabala.gestion_academica.business.impl.AlumnoServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AlumnoDto;
import tomas.aguirrezabala.gestion_academica.persistence.AlumnoDao;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

public class AlumnoServiceImplTest {

    @Mock
    private AlumnoDao alumnoDao;
    
    @Mock
    private AsignaturaDao asignaturaDao;
    
    @Mock
    private MateriaDao materiaDao;
    
    @InjectMocks
    private AlumnoServiceImpl alumnoService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void buscarPorId_debeRetornarAlumno_cuandoExiste() {
        Long idBuscado = 1L;
        Alumno alumnoSimulado = new Alumno();
        alumnoSimulado.setId(idBuscado);
        alumnoSimulado.setNombre("Tomas");
        alumnoSimulado.setApellido("Aguirrezabala");
        
        when(alumnoDao.buscarPorId(idBuscado)).thenReturn(Optional.of(alumnoSimulado));
        
        Optional<Alumno> resultado = alumnoService.buscarPorId(idBuscado);
        
        assertTrue(resultado.isPresent(), "El resultado debería contener un alumno");
        assertEquals("Tomas", resultado.get().getNombre(), "El nombre del alumno debe ser correcto");
        assertEquals("Aguirrezabala", resultado.get().getApellido(), "El apellido del alumno debe ser correcto");
        
        verify(alumnoDao).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeRetornarVacio_cuandoNoExiste() {
        
        Long idInexistente = 999L;
        
        when(alumnoDao.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        Optional<Alumno> resultado = alumnoService.buscarPorId(idInexistente);
        
        assertTrue(resultado.isEmpty(), "El resultado debería estar vacío");
        
        verify(alumnoDao).buscarPorId(idInexistente);
    }

    @Test
    void guardar_debeCrearNuevoAlumno_cuandoDatosValidos() throws EntidadDuplicadaException {
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("87654321");
        
        Alumno alumnoEsperado = new Alumno();
        alumnoEsperado.setId(1L); 
        alumnoEsperado.setNombre("Tomas");
        alumnoEsperado.setApellido("Aguirrezabala");
        alumnoEsperado.setDni("87654321");

        List<Alumno> alumnosExistentes = new ArrayList<>();
        
        when(alumnoDao.buscarTodos()).thenReturn(alumnosExistentes);
        when(alumnoDao.guardar(any(Alumno.class))).thenReturn(alumnoEsperado);
        
        Alumno resultado = alumnoService.guardar(alumnoDto);
        
        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(1L, resultado.getId(), "El ID del alumno debe ser el asignado");
        assertEquals("Tomas", resultado.getNombre(), "El nombre del alumno debe ser correcto");
        assertEquals("Aguirrezabala", resultado.getApellido(), "El apellido del alumno debe ser correcto");
        assertEquals("87654321", resultado.getDni(), "El DNI del alumno debe ser correcto");
        
        verify(alumnoDao).buscarTodos();
        verify(alumnoDao).guardar(any(Alumno.class));
    }
    
    @Test
    void guardar_debeRechazarCreacion_cuandoDniDuplicado() {
      
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas1");
        alumnoDto.setApellido("Aaaaa");
        alumnoDto.setDni("12345678"); 
        
        Alumno alumnoExistente = new Alumno();
        alumnoExistente.setId(1L);
        alumnoExistente.setNombre("Tomas2");
        alumnoExistente.setApellido("bbbb");
        alumnoExistente.setDni("12345678");
        
        List<Alumno> alumnosExistentes = Arrays.asList(alumnoExistente);
        
        when(alumnoDao.buscarTodos()).thenReturn(alumnosExistentes);
        
        EntidadDuplicadaException excepcion = assertThrows(
            EntidadDuplicadaException.class,
            () -> alumnoService.guardar(alumnoDto),
            "Debería lanzar una excepción por DNI duplicado"
        );
        
        assertTrue(excepcion.getMessage().contains("DNI"), 
                "El mensaje debería mencionar el DNI como causa del problema");
        assertTrue(excepcion.getMessage().contains("12345678"), 
                "El mensaje debería contener el valor del DNI duplicado");
        
        verify(alumnoDao).buscarTodos();
        verify(alumnoDao, never()).guardar(any(Alumno.class));
    }
    
    @Test
    void guardar_debeActualizarAlumno_cuandoIdExistente() throws EntidadDuplicadaException {
        
        Long alumnoId = 1L;
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setId(alumnoId);
        alumnoDto.setNombre("Tomas Actualizado");
        alumnoDto.setApellido("Aguirrezabala Modificado");
        alumnoDto.setDni("12345678");
        
        Alumno alumnoActualizado = new Alumno();
        alumnoActualizado.setId(alumnoId);
        alumnoActualizado.setNombre("Tomas Actualizado");
        alumnoActualizado.setApellido("Aguirrezabala Modificado");
        alumnoActualizado.setDni("12345678");
        
        when(alumnoDao.guardar(any(Alumno.class))).thenReturn(alumnoActualizado);
        
        Alumno resultado = alumnoService.guardar(alumnoDto);
        
        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(alumnoId, resultado.getId(), "El ID del alumno debe mantenerse");
        assertEquals("Tomas Actualizado", resultado.getNombre(), "El nombre del alumno debe actualizarse");
        assertEquals("Aguirrezabala Modificado", resultado.getApellido(), "El apellido del alumno debe actualizarse");
        
        verify(alumnoDao).guardar(any(Alumno.class));
    }

    @Test
    void eliminarPorId_debeEliminarAlumno_cuandoNoTieneAsignaturas() throws EntidadNoEncontradaException, ReglaNegocioException {

    Long alumnoId = 1L;
    
    Alumno alumno = new Alumno();
    alumno.setId(alumnoId);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
    
    when(asignaturaDao.buscarPorAlumnoId(alumnoId)).thenReturn(new ArrayList<>());
    
    alumnoService.eliminarPorId(alumnoId);
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(asignaturaDao).buscarPorAlumnoId(alumnoId);
    verify(alumnoDao).borrarPorId(alumnoId);
}
@Test
void eliminarPorId_debeFallar_cuandoTieneAsignaturas() {
  
    Long alumnoId = 1L;
    
    Alumno alumno = new Alumno();
    alumno.setId(alumnoId);
    alumno.setNombre("Tomas");
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
    
    List<Asignatura> asignaturas = new ArrayList<>();
    asignaturas.add(new Asignatura()); 
    
    when(asignaturaDao.buscarPorAlumnoId(alumnoId)).thenReturn(asignaturas);
    
    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> alumnoService.eliminarPorId(alumnoId),
        "Debería lanzar una excepción porque tiene asignaturas asociadas"
    );
    
    assertTrue(exception.getMessage().contains("tiene asignaturas asociadas"),
              "El mensaje debe indicar que tiene asignaturas asociadas");
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(asignaturaDao).buscarPorAlumnoId(alumnoId);

    verify(alumnoDao, never()).borrarPorId(alumnoId);
}

@Test
void eliminarPorId_debeFallar_cuandoAlumnoNoExiste() {

    Long alumnoId = 999L;
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.empty());
    
    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> alumnoService.eliminarPorId(alumnoId),
        "Debería lanzar una excepción porque el alumno no existe"
    );
    
    assertTrue(exception.getMessage().contains("Alumno"),
              "El mensaje debe mencionar que el alumno no fue encontrado");
    assertTrue(exception.getMessage().contains(String.valueOf(alumnoId)),
              "El mensaje debe mencionar el ID del alumno");
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(asignaturaDao, never()).buscarPorAlumnoId(anyLong());
    verify(alumnoDao, never()).borrarPorId(anyLong());
}
@Test
void buscarTodos_debeRetornarListaDeAlumnos_cuandoHayAlumnos() {

    List<Alumno> alumnosSimulados = new ArrayList<>();
    
    Alumno alumno1 = new Alumno();
    alumno1.setId(1L);
    alumno1.setNombre("Tomas");
    alumno1.setApellido("Aguirrezabala");
    alumno1.setDni("12345678");
    
    Alumno alumno2 = new Alumno();
    alumno2.setId(2L);
    alumno2.setNombre("tomas2");
    alumno2.setApellido("aaaaa");
    alumno2.setDni("87654321");
    
    alumnosSimulados.add(alumno1);
    alumnosSimulados.add(alumno2);
    
    when(alumnoDao.buscarTodos()).thenReturn(alumnosSimulados);
    
    List<Alumno> resultado = alumnoService.buscarTodos();
    
    assertNotNull(resultado, "El resultado no debería ser null");
    assertEquals(2, resultado.size(), "Deberían haber 2 alumnos");
    
    assertEquals(1L, resultado.get(0).getId(), "El ID del primer alumno debe ser correcto");
    assertEquals("Tomas", resultado.get(0).getNombre(), "El nombre del primer alumno debe ser correcto");
    assertEquals("Aguirrezabala", resultado.get(0).getApellido(), "El apellido del primer alumno debe ser correcto");
    
    assertEquals(2L, resultado.get(1).getId(), "El ID del segundo alumno debe ser correcto");
    assertEquals("tomas2", resultado.get(1).getNombre(), "El nombre del segundo alumno debe ser correcto");
    assertEquals("aaaaa", resultado.get(1).getApellido(), "El apellido del segundo alumno debe ser correcto");
    
    verify(alumnoDao).buscarTodos();
}

@Test
void buscarTodos_debeRetornarListaVacia_cuandoNoHayAlumnos() {

    when(alumnoDao.buscarTodos()).thenReturn(new ArrayList<>());
    
    List<Alumno> resultado = alumnoService.buscarTodos();
    
    assertNotNull(resultado, "El resultado no debería ser null, sino una lista vacía");
    assertTrue(resultado.isEmpty(), "La lista debería estar vacía");

    verify(alumnoDao).buscarTodos();
}


@Test
void inscribirEnMateria_debeInscribirAlumno_cuandoDatosValidos() 
        throws EntidadNoEncontradaException, EntidadDuplicadaException, ReglaNegocioException {

    Long alumnoId = 1L;
    Long materiaId = 2L;
    
    Alumno alumno = new Alumno();
    alumno.setId(alumnoId);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
    when(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumnoId, materiaId)).thenReturn(Optional.empty());
    
    Asignatura asignaturaCreada = new Asignatura();
    asignaturaCreada.setId(1L);
    asignaturaCreada.setAlumno(alumno);
    asignaturaCreada.setMateria(materia);
    asignaturaCreada.setEstado(EstadoAsignatura.CURSANDO);
    
    when(asignaturaDao.guardar(any(Asignatura.class))).thenReturn(asignaturaCreada);
    
    Asignatura resultado = alumnoService.inscribirEnMateria(alumnoId, materiaId);
    
    assertNotNull(resultado, "El resultado no debería ser null");
    assertEquals(1L, resultado.getId(), "El ID de la asignatura debe ser el asignado");
    assertEquals(alumno, resultado.getAlumno(), "El alumno de la asignatura debe ser correcto");
    assertEquals(materia, resultado.getMateria(), "La materia de la asignatura debe ser correcta");
    assertEquals(EstadoAsignatura.CURSANDO, resultado.getEstado(), "El estado de la asignatura debe ser CURSANDO");
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(asignaturaDao).buscarPorAlumnoIdYMateriaId(alumnoId, materiaId);
    verify(asignaturaDao).guardar(any(Asignatura.class));
}

@Test
void inscribirEnMateria_debeFallar_cuandoAlumnoNoExiste() {

    Long alumnoId = 999L; 
    Long materiaId = 1L;
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.empty());
    
    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> alumnoService.inscribirEnMateria(alumnoId, materiaId),
        "Debería lanzar una excepción cuando el alumno no existe"
    );
    
    assertTrue(exception.getMessage().contains("Alumno"),
            "El mensaje debe mencionar que el alumno no fue encontrado");
    assertTrue(exception.getMessage().contains(String.valueOf(alumnoId)),
            "El mensaje debe mencionar el ID del alumno");
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(materiaDao, never()).buscarPorId(anyLong());
    verify(asignaturaDao, never()).guardar(any(Asignatura.class));
}

@Test
void inscribirEnMateria_debeFallar_cuandoMateriaNoExiste() {

    Long alumnoId = 1L;
    Long materiaId = 999L; 
    
    Alumno alumno = new Alumno();
    alumno.setId(alumnoId);
    alumno.setNombre("Tomas");
    
    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());
    
    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> alumnoService.inscribirEnMateria(alumnoId, materiaId),
        "Debería lanzar una excepción cuando la materia no existe"
    );
    
    assertTrue(exception.getMessage().contains("Materia"),
            "El mensaje debe mencionar que la materia no fue encontrada");
    assertTrue(exception.getMessage().contains(String.valueOf(materiaId)),
            "El mensaje debe mencionar el ID de la materia");
    
    
    verify(alumnoDao).buscarPorId(alumnoId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(asignaturaDao, never()).buscarPorAlumnoIdYMateriaId(anyLong(), anyLong());
    verify(asignaturaDao, never()).guardar(any(Asignatura.class));
}

@Test
void inscribirEnMateria_debeFallar_cuandoYaEstaInscrito() {

    Long alumnoId = 1L;
    Long materiaId = 2L;

    Alumno alumno = new Alumno();
    alumno.setId(alumnoId);
    alumno.setNombre("Tomas");

    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");

    Asignatura asignaturaExistente = new Asignatura();
    asignaturaExistente.setId(1L);
    asignaturaExistente.setAlumno(alumno);
    asignaturaExistente.setMateria(materia);

    when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
    when(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumnoId, materiaId)).thenReturn(Optional.of(asignaturaExistente));

    EntidadDuplicadaException exception = assertThrows(
        EntidadDuplicadaException.class,
        () -> alumnoService.inscribirEnMateria(alumnoId, materiaId),
        "Debería lanzar una excepción cuando el alumno ya está inscrito en la materia"
    );
    
    assertTrue(exception.getMessage().contains("ya está inscrito"),
            "El mensaje debe mencionar que ya está inscrito");

    verify(alumnoDao).buscarPorId(alumnoId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(asignaturaDao).buscarPorAlumnoIdYMateriaId(alumnoId, materiaId);
    verify(asignaturaDao, never()).guardar(any(Asignatura.class));
}
}