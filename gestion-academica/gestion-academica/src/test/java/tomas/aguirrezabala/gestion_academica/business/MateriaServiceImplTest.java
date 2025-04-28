package tomas.aguirrezabala.gestion_academica.business;

import java.util.ArrayList;
import java.util.Arrays;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tomas.aguirrezabala.gestion_academica.business.impl.MateriaServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.MateriaDto;
import tomas.aguirrezabala.gestion_academica.persistence.AsignaturaDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;
import tomas.aguirrezabala.gestion_academica.persistence.ProfesorDao;

public class MateriaServiceImplTest {

    @Mock
    private MateriaDao materiaDao;
    
    @Mock
    private ProfesorDao profesorDao;
    
    @Mock
    private AsignaturaDao asignaturaDao;
    
    @InjectMocks
    private MateriaServiceImpl materiaService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void guardar_debeCrearMateriaNueva_cuandoDatosValidos() throws EntidadDuplicadaException, EntidadNoEncontradaException {
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        
        when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());
        
        Materia materiaGuardada = new Materia();
        materiaGuardada.setId(1L);
        materiaGuardada.setNombre("Programación I");
        materiaGuardada.setAnio(1);
        materiaGuardada.setCuatrimestre(1);
        
        when(materiaDao.guardar(any(Materia.class))).thenReturn(materiaGuardada);
        
        Materia resultado = materiaService.guardar(materiaDto);

    
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Programación I", resultado.getNombre());
        assertEquals(1, resultado.getAnio());
        assertEquals(1, resultado.getCuatrimestre());
        
        verify(materiaDao).buscarAll();
        verify(materiaDao).guardar(any(Materia.class));
    }
    
    @Test
    void guardar_debeRechazarCreacion_cuandoNombreDuplicado() {
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        
        Materia materiaExistente = new Materia();
        materiaExistente.setId(1L);
        materiaExistente.setNombre("Programación I");
        
        List<Materia> materias = Arrays.asList(materiaExistente);
        when(materiaDao.buscarAll()).thenReturn(materias);

        Exception exception = assertThrows(EntidadDuplicadaException.class, () -> {
            materiaService.guardar(materiaDto);
        });
        
        assertTrue(exception.getMessage().contains("nombre"));
        assertTrue(exception.getMessage().contains("Programación I"));
        
        verify(materiaDao).buscarAll();
        verify(materiaDao, never()).guardar(any(Materia.class));
    }
    
    @Test
    void guardar_debeAsignarProfesor_cuandoProfesorIdValido() throws EntidadDuplicadaException, EntidadNoEncontradaException {
        Long profesorId = 1L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación II");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(2);
        materiaDto.setProfesorId(profesorId);

        when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

        Profesor profesor = new Profesor();
        profesor.setId(profesorId);
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        
        when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

        Materia materiaGuardada = new Materia();
        materiaGuardada.setId(1L);
        materiaGuardada.setNombre("Programación II");
        materiaGuardada.setAnio(1);
        materiaGuardada.setCuatrimestre(2);
        materiaGuardada.setProfesor(profesor);
        
        when(materiaDao.guardar(any(Materia.class))).thenReturn(materiaGuardada);

        Materia resultado = materiaService.guardar(materiaDto);

        assertNotNull(resultado);
        assertEquals("Programación II", resultado.getNombre());
        assertNotNull(resultado.getProfesor());
        assertEquals(profesorId, resultado.getProfesor().getId());
        assertEquals("Tomas", resultado.getProfesor().getNombre());
        
        verify(materiaDao).buscarAll();
        verify(profesorDao).buscarPorId(profesorId);
        verify(materiaDao).guardar(any(Materia.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoProfesorNoExiste() {

        Long profesorId = 999L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación III");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(profesorId);
        
        when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

        when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntidadNoEncontradaException.class, () -> {
            materiaService.guardar(materiaDto);
        });
        
        assertTrue(exception.getMessage().contains("Profesor"));
        assertTrue(exception.getMessage().contains(profesorId.toString()));
        
        verify(materiaDao).buscarAll();
        verify(profesorDao).buscarPorId(profesorId);
        verify(materiaDao, never()).guardar(any(Materia.class));
    }
    
    @Test
    void guardar_debeAsignarCorrelatividades_cuandoCorrelativasValidas() throws EntidadDuplicadaException, EntidadNoEncontradaException {
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación IV");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(2);

        List<Long> correlatividades = Arrays.asList(1L, 2L);
        materiaDto.setCorrelatividades(correlatividades);

        when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

        Materia correlativa1 = new Materia();
        correlativa1.setId(1L);
        correlativa1.setNombre("Programación I");
        
        Materia correlativa2 = new Materia();
        correlativa2.setId(2L);
        correlativa2.setNombre("Programación II");
        
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(correlativa1));
        when(materiaDao.buscarPorId(2L)).thenReturn(Optional.of(correlativa2));

        Materia materiaGuardada = new Materia();
        materiaGuardada.setId(3L);
        materiaGuardada.setNombre("Programación IV");
        materiaGuardada.setAnio(2);
        materiaGuardada.setCuatrimestre(2);
        materiaGuardada.setCorrelatividades(correlatividades);
        
        when(materiaDao.guardar(any(Materia.class))).thenReturn(materiaGuardada);

        Materia resultado = materiaService.guardar(materiaDto);

        assertNotNull(resultado);
        assertEquals("Programación IV", resultado.getNombre());
        assertEquals(2, resultado.getCorrelatividades().size());
        assertTrue(resultado.getCorrelatividades().contains(1L));
        assertTrue(resultado.getCorrelatividades().contains(2L));
        
        verify(materiaDao).buscarAll();
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(2L);
        verify(materiaDao).guardar(any(Materia.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoCorrelativaNoExiste() {
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación IV");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(2);

        List<Long> correlatividades = Arrays.asList(1L, 999L);
        materiaDto.setCorrelatividades(correlatividades);

        when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

        Materia correlativa1 = new Materia();
        correlativa1.setId(1L);
        correlativa1.setNombre("Programación I");
        
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(correlativa1));
        when(materiaDao.buscarPorId(999L)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntidadNoEncontradaException.class, () -> {
            materiaService.guardar(materiaDto);
        });
        
        assertTrue(exception.getMessage().contains("correlativa"));
        assertTrue(exception.getMessage().contains("999"));
        
        verify(materiaDao).buscarAll();
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(999L);
        verify(materiaDao, never()).guardar(any(Materia.class));
    }

@Test
void buscarPorId_debeRetornarMateria_cuandoExiste() {
    Long materiaId = 1L;

    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");
    materia.setAnio(1);
    materia.setCuatrimestre(1);

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));

    Optional<Materia> resultado = materiaService.buscarPorId(materiaId);

    assertTrue(resultado.isPresent(), "El resultado debería contener una materia");
    assertEquals(materiaId, resultado.get().getId(), "El ID de la materia debe ser correcto");
    assertEquals("Programación I", resultado.get().getNombre(), "El nombre de la materia debe ser correcto");
    assertEquals(1, resultado.get().getAnio(), "El año de la materia debe ser correcto");
    assertEquals(1, resultado.get().getCuatrimestre(), "El cuatrimestre de la materia debe ser correcto");

    verify(materiaDao).buscarPorId(materiaId);
}

@Test
void buscarPorId_debeRetornarVacio_cuandoNoExiste() {

    Long materiaId = 999L; 

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());

    Optional<Materia> resultado = materiaService.buscarPorId(materiaId);

    assertFalse(resultado.isPresent(), "El resultado debería estar vacío");

    verify(materiaDao).buscarPorId(materiaId);
}

@Test
void buscarTodas_debeRetornarListaDeMaterias_cuandoHayMaterias() {
    List<Materia> materiasSimuladas = new ArrayList<>();
    
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    materia1.setAnio(1);
    materia1.setCuatrimestre(1);
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Estadistica");
    materia2.setAnio(1);
    materia2.setCuatrimestre(2);
    
    materiasSimuladas.add(materia1);
    materiasSimuladas.add(materia2);

    when(materiaDao.buscarAll()).thenReturn(materiasSimuladas);

    List<Materia> resultado = materiaService.buscarTodas();

    assertNotNull(resultado, "El resultado no debería ser null");
    assertEquals(2, resultado.size(), "Deberían haber 2 materias");

    assertEquals(1L, resultado.get(0).getId(), "El ID de la primera materia debe ser correcto");
    assertEquals("Programación I", resultado.get(0).getNombre(), "El nombre de la primera materia debe ser correcto");
    assertEquals(1, resultado.get(0).getAnio(), "El año de la primera materia debe ser correcto");

    assertEquals(2L, resultado.get(1).getId(), "El ID de la segunda materia debe ser correcto");
    assertEquals("Estadistica", resultado.get(1).getNombre(), "El nombre de la segunda materia debe ser correcto");
    assertEquals(2, resultado.get(1).getCuatrimestre(), "El cuatrimestre de la segunda materia debe ser correcto");

    verify(materiaDao).buscarAll();
}

@Test
void buscarTodas_debeRetornarListaVacia_cuandoNoHayMaterias() {

    when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

    List<Materia> resultado = materiaService.buscarTodas();

    assertNotNull(resultado, "El resultado no debería ser null, sino una lista vacía");
    assertTrue(resultado.isEmpty(), "La lista debería estar vacía");

    verify(materiaDao).buscarAll();
}

@Test
void eliminarPorId_debeEliminarMateria_cuandoNoTieneDependencias() throws EntidadNoEncontradaException, ReglaNegocioException {
    Long materiaId = 1L;

    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));

    when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

    when(asignaturaDao.existePorMateriaId(materiaId)).thenReturn(false);

    materiaService.eliminarPorId(materiaId);

    verify(materiaDao).buscarPorId(materiaId);
    verify(materiaDao).buscarAll();
    verify(asignaturaDao).existePorMateriaId(materiaId);
    verify(materiaDao).borrarPorId(materiaId);
}

@Test
void eliminarPorId_debeFallar_cuandoEsCorrelativaDeOtraMateria() {
    Long materiaId = 1L;

    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");

    Materia materiaAvanzada = new Materia();
    materiaAvanzada.setId(2L);
    materiaAvanzada.setNombre("Programación II");
    materiaAvanzada.setCorrelatividades(Arrays.asList(materiaId));

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));
    when(materiaDao.buscarAll()).thenReturn(Arrays.asList(materia, materiaAvanzada));

    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> materiaService.eliminarPorId(materiaId),
        "Debería lanzar una excepción por ser correlativa de otra materia"
    );
    
    assertTrue(exception.getMessage().contains("correlativa"));
    assertTrue(exception.getMessage().contains("Programación II"));
    
    verify(materiaDao).buscarPorId(materiaId);
    verify(materiaDao).buscarAll();
    verify(materiaDao, never()).borrarPorId(materiaId);
}

@Test
void eliminarPorId_debeFallar_cuandoTieneAlumnosInscriptos() {
    Long materiaId = 1L;

    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));

    when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

    when(asignaturaDao.existePorMateriaId(materiaId)).thenReturn(true);

    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> materiaService.eliminarPorId(materiaId),
        "Debería lanzar una excepción por tener alumnos inscriptos"
    );
    
    assertTrue(exception.getMessage().contains("alumnos inscriptos"));
    
    verify(materiaDao).buscarPorId(materiaId);
    verify(materiaDao).buscarAll();
    verify(asignaturaDao).existePorMateriaId(materiaId);
    verify(materiaDao, never()).borrarPorId(materiaId);
}

@Test
void eliminarPorId_debeFallar_cuandoMateriaNoExiste() {
    Long materiaId = 999L;

    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> materiaService.eliminarPorId(materiaId),
        "Debería lanzar una excepción porque la materia no existe"
    );
    
    assertTrue(exception.getMessage().contains("Materia"));
    assertTrue(exception.getMessage().contains(materiaId.toString()));
    
    verify(materiaDao).buscarPorId(materiaId);
    verify(materiaDao, never()).buscarAll();
    verify(asignaturaDao, never()).existePorMateriaId(anyLong());
    verify(materiaDao, never()).borrarPorId(materiaId);
}


@Test
void crearConCorrelatividades_debeCrearMateriaConCorrelativas_cuandoCorrelativasValidas() 
        throws EntidadNoEncontradaException, ReglaNegocioException {

    Long materiaId = 3L;
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación Avanzada");
    materia.setAnio(3);
    materia.setCuatrimestre(1);

    List<Long> correlatividades = Arrays.asList(1L, 2L);

    Materia correlativa1 = new Materia();
    correlativa1.setId(1L);
    correlativa1.setNombre("Programación I");
    
    Materia correlativa2 = new Materia();
    correlativa2.setId(2L);
    correlativa2.setNombre("Programación II");

    when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(correlativa1));
    when(materiaDao.buscarPorId(2L)).thenReturn(Optional.of(correlativa2));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());

    Materia materiaGuardada = new Materia();
    materiaGuardada.setId(materiaId);
    materiaGuardada.setNombre("Programación Avanzada");
    materiaGuardada.setAnio(3);
    materiaGuardada.setCuatrimestre(1);
    materiaGuardada.setCorrelatividades(correlatividades);
    
    when(materiaDao.guardar(any(Materia.class))).thenReturn(materiaGuardada);

    Materia resultado = materiaService.crearConCorrelatividades(materia, correlatividades);

    assertNotNull(resultado);
    assertEquals(materiaId, resultado.getId());
    assertEquals("Programación Avanzada", resultado.getNombre());
    assertEquals(2, resultado.getCorrelatividades().size());
    assertTrue(resultado.getCorrelatividades().contains(1L));
    assertTrue(resultado.getCorrelatividades().contains(2L));

    verify(materiaDao, atLeastOnce()).buscarPorId(1L);
    verify(materiaDao, atLeastOnce()).buscarPorId(2L);
    verify(materiaDao).guardar(any(Materia.class));
}

@Test
void crearConCorrelatividades_debeFallar_cuandoCorrelativaNoExiste() {

    Long materiaId = 3L;
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación Avanzada");
    materia.setAnio(3);
    materia.setCuatrimestre(1);

    List<Long> correlatividades = Arrays.asList(1L, 999L);

    Materia correlativa1 = new Materia();
    correlativa1.setId(1L);
    correlativa1.setNombre("Programación I");

    when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(correlativa1));
    when(materiaDao.buscarPorId(999L)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> materiaService.crearConCorrelatividades(materia, correlatividades),
        "Debería lanzar una excepción cuando una correlativa no existe"
    );
    
    assertTrue(exception.getMessage().contains("correlativa"));
    assertTrue(exception.getMessage().contains("999"));

    verify(materiaDao, atLeastOnce()).buscarPorId(1L);
    verify(materiaDao, atLeastOnce()).buscarPorId(999L);
    verify(materiaDao, never()).guardar(any(Materia.class));
}

@Test
void crearConCorrelatividades_debeFallar_cuandoCreaUnCiclo() {

    
    Long materiaId = 2L;
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación II");
    materia.setAnio(2);
    materia.setCuatrimestre(1);

    List<Long> correlatividades = Arrays.asList(1L);

    Materia correlativa1 = new Materia();
    correlativa1.setId(1L);
    correlativa1.setNombre("Programación I");
    correlativa1.setCorrelatividades(Arrays.asList(materiaId));

    when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(correlativa1));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));

    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> materiaService.crearConCorrelatividades(materia, correlatividades),
        "Debería lanzar una excepción cuando se crea un ciclo de correlatividades"
    );
    
    assertTrue(exception.getMessage().contains("ciclo"));

    verify(materiaDao, atLeastOnce()).buscarPorId(1L);
    verify(materiaDao, never()).guardar(any(Materia.class));
}

@Test
void crearConCorrelatividades_debeCrearMateriaSinCorrelativas_cuandoListaVacia() 
        throws EntidadNoEncontradaException, ReglaNegocioException {
    Long materiaId = 1L;
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");
    materia.setAnio(1);
    materia.setCuatrimestre(1);

    List<Long> correlatividades = new ArrayList<>();

    Materia materiaGuardada = new Materia();
    materiaGuardada.setId(materiaId);
    materiaGuardada.setNombre("Programación I");
    materiaGuardada.setAnio(1);
    materiaGuardada.setCuatrimestre(1);
    materiaGuardada.setCorrelatividades(correlatividades);
    
    when(materiaDao.guardar(any(Materia.class))).thenReturn(materiaGuardada);

    Materia resultado = materiaService.crearConCorrelatividades(materia, correlatividades);

    assertNotNull(resultado);
    assertEquals(materiaId, resultado.getId());
    assertEquals("Programación I", resultado.getNombre());
    assertTrue(resultado.getCorrelatividades().isEmpty(), "La lista de correlatividades debería estar vacía");
    
    verify(materiaDao, never()).buscarPorId(anyLong());
    verify(materiaDao).guardar(any(Materia.class));
}
}