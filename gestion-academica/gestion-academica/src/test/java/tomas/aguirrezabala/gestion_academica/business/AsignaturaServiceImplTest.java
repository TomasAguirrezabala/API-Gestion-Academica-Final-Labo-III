package tomas.aguirrezabala.gestion_academica.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import tomas.aguirrezabala.gestion_academica.business.impl.AsignaturaServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AsignaturaDto;
import tomas.aguirrezabala.gestion_academica.persistence.AlumnoDao;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

public class AsignaturaServiceImplTest {
    
    @Mock
    private AsignaturaDao asignaturaDao;
    
    @Mock
    private AlumnoDao alumnoDao;
    
    @Mock
    private MateriaDao materiaDao;
    
    @InjectMocks
    private AsignaturaServiceImpl asignaturaService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void guardar_debeCrearAsignatura_cuandoDatosValidos() throws EntidadNoEncontradaException, EntidadDuplicadaException {

        Long alumnoId = 1L;
        Long materiaId = 1L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);

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

        Asignatura asignaturaGuardada = new Asignatura();
        asignaturaGuardada.setId(1L);
        asignaturaGuardada.setAlumno(alumno);
        asignaturaGuardada.setMateria(materia);
        asignaturaGuardada.setEstado(EstadoAsignatura.CURSANDO);
        
        when(asignaturaDao.guardar(any(Asignatura.class))).thenReturn(asignaturaGuardada);

        Asignatura resultado = asignaturaService.guardar(asignaturaDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(alumnoId, resultado.getAlumno().getId());
        assertEquals("Tomas", resultado.getAlumno().getNombre());
        assertEquals("Aguirrezabala", resultado.getAlumno().getApellido());
        assertEquals(materiaId, resultado.getMateria().getId());
        assertEquals("Programación I", resultado.getMateria().getNombre());
        assertEquals(EstadoAsignatura.CURSANDO, resultado.getEstado());
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao).buscarPorId(materiaId);
        verify(asignaturaDao).buscarPorAlumnoIdYMateriaId(alumnoId, materiaId);
        verify(asignaturaDao).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoAlumnoNoExiste() {

        Long alumnoId = 999L;
        Long materiaId = 1L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.empty());

        EntidadNoEncontradaException exception = assertThrows(
            EntidadNoEncontradaException.class,
            () -> asignaturaService.guardar(asignaturaDto),
            "Debería lanzar excepción cuando el alumno no existe"
        );
        
        assertTrue(exception.getMessage().contains("Alumno"));
        assertTrue(exception.getMessage().contains(alumnoId.toString()));
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao, never()).buscarPorId(anyLong());
        verify(asignaturaDao, never()).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoMateriaNoExiste() {

        Long alumnoId = 1L;
        Long materiaId = 999L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);

        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());

        EntidadNoEncontradaException exception = assertThrows(
            EntidadNoEncontradaException.class,
            () -> asignaturaService.guardar(asignaturaDto),
            "Debería lanzar excepción cuando la materia no existe"
        );
        
        assertTrue(exception.getMessage().contains("Materia"));
        assertTrue(exception.getMessage().contains(materiaId.toString()));
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao).buscarPorId(materiaId);
        verify(asignaturaDao, never()).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoAsignaturaDuplicada() {

        Long alumnoId = 1L;
        Long materiaId = 1L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");

        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Programación I");

        Asignatura asignaturaExistente = new Asignatura();
        asignaturaExistente.setId(5L);
        asignaturaExistente.setAlumno(alumno);
        asignaturaExistente.setMateria(materia);
        asignaturaExistente.setEstado(EstadoAsignatura.CURSANDO);
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
        when(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumnoId, materiaId)).thenReturn(Optional.of(asignaturaExistente));

        EntidadDuplicadaException exception = assertThrows(
            EntidadDuplicadaException.class,
            () -> asignaturaService.guardar(asignaturaDto),
            "Debería lanzar excepción cuando ya existe una asignatura para el alumno y materia"
        );
        
        assertTrue(exception.getMessage().contains("Asignatura"));
        assertTrue(exception.getMessage().contains("alumno y materia"));
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao).buscarPorId(materiaId);
        verify(asignaturaDao).buscarPorAlumnoIdYMateriaId(alumnoId, materiaId);
        verify(asignaturaDao, never()).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeActualizarAsignatura_cuandoTieneId() throws EntidadNoEncontradaException, EntidadDuplicadaException {

        Long asignaturaId = 1L;
        Long alumnoId = 2L;
        Long materiaId = 2L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setId(asignaturaId);
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.APROBADO);
        asignaturaDto.setNota(8.0);

        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Tomas2");
        alumno.setApellido("Aguirrezabala2");

        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Programación II");

        Asignatura asignaturaExistente = new Asignatura();
        asignaturaExistente.setId(asignaturaId);
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
        when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.of(asignaturaExistente));

        Asignatura asignaturaActualizada = new Asignatura();
        asignaturaActualizada.setId(asignaturaId);
        asignaturaActualizada.setAlumno(alumno);
        asignaturaActualizada.setMateria(materia);
        asignaturaActualizada.setEstado(EstadoAsignatura.APROBADO);
        asignaturaActualizada.setNota(8.0);
        
        when(asignaturaDao.guardar(any(Asignatura.class))).thenReturn(asignaturaActualizada);

        Asignatura resultado = asignaturaService.guardar(asignaturaDto);

        assertNotNull(resultado);
        assertEquals(asignaturaId, resultado.getId());
        assertEquals(alumnoId, resultado.getAlumno().getId());
        assertEquals(materiaId, resultado.getMateria().getId());
        assertEquals(EstadoAsignatura.APROBADO, resultado.getEstado());
        assertEquals(8.0, resultado.getNota());
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao).buscarPorId(materiaId);
        verify(asignaturaDao).buscarPorId(asignaturaId);
        verify(asignaturaDao, never()).buscarPorAlumnoIdYMateriaId(anyLong(), anyLong());
        verify(asignaturaDao).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeGuardarAsignaturaConEstadoRegular() throws EntidadNoEncontradaException, EntidadDuplicadaException {

        Long alumnoId = 3L;
        Long materiaId = 3L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.REGULAR);

        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Tomas3");
        alumno.setApellido("Aguirrezabala3");

        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Programación III");
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
        when(asignaturaDao.buscarPorAlumnoIdYMateriaId(alumnoId, materiaId)).thenReturn(Optional.empty());

        Asignatura asignaturaGuardada = new Asignatura();
        asignaturaGuardada.setId(3L);
        asignaturaGuardada.setAlumno(alumno);
        asignaturaGuardada.setMateria(materia);
        asignaturaGuardada.setEstado(EstadoAsignatura.REGULAR);
        
        when(asignaturaDao.guardar(any(Asignatura.class))).thenReturn(asignaturaGuardada);

        Asignatura resultado = asignaturaService.guardar(asignaturaDto);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals(EstadoAsignatura.REGULAR, resultado.getEstado());
        
        verify(asignaturaDao).guardar(any(Asignatura.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoAsignaturaParaActualizarNoExiste() {

        Long asignaturaId = 999L;
        Long alumnoId = 1L;
        Long materiaId = 1L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setId(asignaturaId);
        asignaturaDto.setAlumnoId(alumnoId);
        asignaturaDto.setMateriaId(materiaId);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);

        Alumno alumno = new Alumno();
        alumno.setId(alumnoId);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");

        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Programación I");
        
        when(alumnoDao.buscarPorId(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
        when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.empty());

        EntidadNoEncontradaException exception = assertThrows(
            EntidadNoEncontradaException.class,
            () -> asignaturaService.guardar(asignaturaDto),
            "Debería lanzar excepción cuando la asignatura a actualizar no existe"
        );
        
        assertTrue(exception.getMessage().contains("Asignatura"));
        assertTrue(exception.getMessage().contains(asignaturaId.toString()));
        
        verify(alumnoDao).buscarPorId(alumnoId);
        verify(materiaDao).buscarPorId(materiaId);
        verify(asignaturaDao).buscarPorId(asignaturaId);
        verify(asignaturaDao, never()).guardar(any(Asignatura.class));
    }

    @Test
void buscarPorId_debeRetornarAsignatura_cuandoExiste() {

    Long asignaturaId = 1L;

    Alumno alumno = new Alumno();
    alumno.setId(1L);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    Materia materia = new Materia();
    materia.setId(1L);
    materia.setNombre("Programación I");
    
    Asignatura asignatura = new Asignatura();
    asignatura.setId(asignaturaId);
    asignatura.setAlumno(alumno);
    asignatura.setMateria(materia);
    asignatura.setEstado(EstadoAsignatura.CURSANDO);

    when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.of(asignatura));

    Optional<Asignatura> resultado = asignaturaService.buscarPorId(asignaturaId);

    assertTrue(resultado.isPresent());
    assertEquals(asignaturaId, resultado.get().getId());
    assertEquals("Tomas", resultado.get().getAlumno().getNombre());
    assertEquals("Aguirrezabala", resultado.get().getAlumno().getApellido());
    assertEquals("Programación I", resultado.get().getMateria().getNombre());
    assertEquals(EstadoAsignatura.CURSANDO, resultado.get().getEstado());
    
    verify(asignaturaDao).buscarPorId(asignaturaId);
}

@Test
void buscarPorId_debeRetornarOptionalVacio_cuandoNoExiste() {

    Long asignaturaId = 999L;

    when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.empty());

    Optional<Asignatura> resultado = asignaturaService.buscarPorId(asignaturaId);

    assertFalse(resultado.isPresent());
    
    verify(asignaturaDao).buscarPorId(asignaturaId);
}

@Test
void buscarTodas_debeRetornarListaDeAsignaturas_cuandoHayAsignaturas() {

    List<Asignatura> asignaturas = new ArrayList<>();

    Alumno alumno1 = new Alumno();
    alumno1.setId(1L);
    alumno1.setNombre("Tomas");
    alumno1.setApellido("Aguirrezabala");
    
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Asignatura asignatura1 = new Asignatura();
    asignatura1.setId(1L);
    asignatura1.setAlumno(alumno1);
    asignatura1.setMateria(materia1);
    asignatura1.setEstado(EstadoAsignatura.CURSANDO);

    Alumno alumno2 = new Alumno();
    alumno2.setId(2L);
    alumno2.setNombre("Tomas2");
    alumno2.setApellido("Aguirrezabala2");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Programación II");
    
    Asignatura asignatura2 = new Asignatura();
    asignatura2.setId(2L);
    asignatura2.setAlumno(alumno2);
    asignatura2.setMateria(materia2);
    asignatura2.setEstado(EstadoAsignatura.APROBADO);
    asignatura2.setNota(8.0);

    asignaturas.add(asignatura1);
    asignaturas.add(asignatura2);

    when(asignaturaDao.buscarTodos()).thenReturn(asignaturas);

    List<Asignatura> resultado = asignaturaService.buscarTodas();

    assertNotNull(resultado);
    assertEquals(2, resultado.size());

    assertEquals(1L, resultado.get(0).getId());
    assertEquals("Tomas", resultado.get(0).getAlumno().getNombre());
    assertEquals("Programación I", resultado.get(0).getMateria().getNombre());
    assertEquals(EstadoAsignatura.CURSANDO, resultado.get(0).getEstado());

    assertEquals(2L, resultado.get(1).getId());
    assertEquals("Tomas2", resultado.get(1).getAlumno().getNombre());
    assertEquals("Programación II", resultado.get(1).getMateria().getNombre());
    assertEquals(EstadoAsignatura.APROBADO, resultado.get(1).getEstado());
    assertEquals(8.0, resultado.get(1).getNota());
    
    verify(asignaturaDao).buscarTodos();
}

@Test
void buscarTodas_debeRetornarListaVacia_cuandoNoHayAsignaturas() {

    List<Asignatura> asignaturas = new ArrayList<>();

    when(asignaturaDao.buscarTodos()).thenReturn(asignaturas);

    List<Asignatura> resultado = asignaturaService.buscarTodas();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    
    verify(asignaturaDao).buscarTodos();
}

@Test
void eliminarPorId_debeEliminarAsignatura_cuandoExiste() throws EntidadNoEncontradaException {

    Long asignaturaId = 1L;

    Alumno alumno = new Alumno();
    alumno.setId(1L);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    Materia materia = new Materia();
    materia.setId(1L);
    materia.setNombre("Programación I");
    
    Asignatura asignatura = new Asignatura();
    asignatura.setId(asignaturaId);
    asignatura.setAlumno(alumno);
    asignatura.setMateria(materia);
    asignatura.setEstado(EstadoAsignatura.CURSANDO);

    when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.of(asignatura));

    asignaturaService.eliminarPorId(asignaturaId);

    verify(asignaturaDao).buscarPorId(asignaturaId);
    verify(asignaturaDao).borrarPorId(asignaturaId);
}

@Test
void eliminarPorId_debeFallar_cuandoNoExiste() {

    Long asignaturaId = 999L;

    when(asignaturaDao.buscarPorId(asignaturaId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> asignaturaService.eliminarPorId(asignaturaId),
        "Debería lanzar excepción cuando la asignatura no existe"
    );
    
    assertTrue(exception.getMessage().contains("Asignatura"));
    assertTrue(exception.getMessage().contains(asignaturaId.toString()));
    
    verify(asignaturaDao).buscarPorId(asignaturaId);
    verify(asignaturaDao, never()).borrarPorId(anyLong());
}

}