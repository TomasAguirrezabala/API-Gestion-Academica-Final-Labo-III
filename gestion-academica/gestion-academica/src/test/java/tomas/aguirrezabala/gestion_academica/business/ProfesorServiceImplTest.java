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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import tomas.aguirrezabala.gestion_academica.business.impl.ProfesorServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.ProfesorDto;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;
import tomas.aguirrezabala.gestion_academica.persistence.ProfesorDao;

public class ProfesorServiceImplTest {
    
    @Mock
    private ProfesorDao profesorDao;
    
    @Mock
    private MateriaDao materiaDao;
    
    @InjectMocks
    private ProfesorServiceImpl profesorService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void guardar_debeCrearProfesor_cuandoDatosValidos() throws EntidadDuplicadaException {
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Tomas");
        profesorDto.setApellido("Aguirrezabala");
        profesorDto.setTitulo("Tecnico Universitario en Programacion");

        when(profesorDao.buscarAll()).thenReturn(new ArrayList<>());

        Profesor profesorGuardado = new Profesor();
        profesorGuardado.setId(1L);
        profesorGuardado.setNombre("Tomas");
        profesorGuardado.setApellido("Aguirrezabala");
        profesorGuardado.setTitulo("Tecnico Universitario en Programacion");
        
        when(profesorDao.guardar(any(Profesor.class))).thenReturn(profesorGuardado);

        Profesor resultado = profesorService.guardar(profesorDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tomas", resultado.getNombre());
        assertEquals("Aguirrezabala", resultado.getApellido());
        assertEquals("Tecnico Universitario en Programacion", resultado.getTitulo());
        
        verify(profesorDao).buscarAll();
        verify(profesorDao).guardar(any(Profesor.class));
    }
    
    @Test
    void guardar_debeCrearProfesorConMaterias_cuandoMateriasExisten() throws EntidadDuplicadaException {
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Tomas");
        profesorDto.setApellido("Aguirrezabala");
        profesorDto.setTitulo("Tecnico Universitario en Programacion");

        List<Long> materiasIds = Arrays.asList(1L, 2L);
        profesorDto.setMateriasIds(materiasIds);

        when(profesorDao.buscarAll()).thenReturn(new ArrayList<>());

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(materia1));
        when(materiaDao.buscarPorId(2L)).thenReturn(Optional.of(materia2));

        Profesor profesorGuardado = new Profesor();
        profesorGuardado.setId(1L);
        profesorGuardado.setNombre("Tomas");
        profesorGuardado.setApellido("Aguirrezabala");
        profesorGuardado.setTitulo("Tecnico Universitario en Programacion");
        
        List<Materia> materias = Arrays.asList(materia1, materia2);
        profesorGuardado.setMaterias(materias);
        
        when(profesorDao.guardar(any(Profesor.class))).thenReturn(profesorGuardado);

        Profesor resultado = profesorService.guardar(profesorDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tomas", resultado.getNombre());
        assertEquals(2, resultado.getMaterias().size());

        assertEquals(1L, resultado.getMaterias().get(0).getId());
        assertEquals("Programación I", resultado.getMaterias().get(0).getNombre());
        
        assertEquals(2L, resultado.getMaterias().get(1).getId());
        assertEquals("Base de Datos", resultado.getMaterias().get(1).getNombre());
        
        verify(profesorDao).buscarAll();
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(2L);
        verify(profesorDao).guardar(any(Profesor.class));
    }
    
    @Test
    void guardar_debeFallar_cuandoProfesorDuplicado() {

        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Tomas");
        profesorDto.setApellido("Aguirrezabala");
        profesorDto.setTitulo("Tecnico Universitario en Programacion");

        Profesor profesorExistente = new Profesor();
        profesorExistente.setId(5L);
        profesorExistente.setNombre("Tomas");
        profesorExistente.setApellido("Aguirrezabala");
        profesorExistente.setTitulo("Tecnico Universitario en Programacion II");
        
        List<Profesor> profesoresExistentes = Arrays.asList(profesorExistente);
        when(profesorDao.buscarAll()).thenReturn(profesoresExistentes);

        EntidadDuplicadaException exception = assertThrows(
            EntidadDuplicadaException.class,
            () -> profesorService.guardar(profesorDto),
            "Debería lanzar excepción por profesor duplicado"
        );
        
        assertTrue(exception.getMessage().contains("nombre y apellido"));
        assertTrue(exception.getMessage().contains("Tomas Aguirrezabala"));
        
        verify(profesorDao).buscarAll();
        verify(profesorDao, never()).guardar(any(Profesor.class));
    }
    
    @Test
    void guardar_debeActualizarProfesor_cuandoTieneId() throws EntidadDuplicadaException {
        Long profesorId = 10L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setId(profesorId);
        profesorDto.setNombre("Tomas");
        profesorDto.setApellido("Aguirrezabala");
        profesorDto.setTitulo("Actualizado");

        Profesor profesorActualizado = new Profesor();
        profesorActualizado.setId(profesorId);
        profesorActualizado.setNombre("Tomas");
        profesorActualizado.setApellido("Aguirrezabala");
        profesorActualizado.setTitulo("Actualizado");
        
        when(profesorDao.guardar(any(Profesor.class))).thenReturn(profesorActualizado);

        Profesor resultado = profesorService.guardar(profesorDto);

        assertNotNull(resultado);
        assertEquals(profesorId, resultado.getId());
        assertEquals("Tomas", resultado.getNombre());
        assertEquals("Aguirrezabala", resultado.getApellido());
        assertEquals("Actualizado", resultado.getTitulo());

        verify(profesorDao, never()).buscarAll();
        verify(profesorDao).guardar(any(Profesor.class));
    }
    
    @Test
    void guardar_debeIgnorarMateriasNoExistentes() throws EntidadDuplicadaException {
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Tomas");
        profesorDto.setApellido("Aguirrezabala");
        profesorDto.setTitulo("Tecnico Universitario en Programacion");

        List<Long> materiasIds = Arrays.asList(1L, 999L);
        profesorDto.setMateriasIds(materiasIds);

        when(profesorDao.buscarAll()).thenReturn(new ArrayList<>());

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Base de Datos");
        
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(materia1));
        when(materiaDao.buscarPorId(999L)).thenReturn(Optional.empty());

        Profesor profesorGuardado = new Profesor();
        profesorGuardado.setId(1L);
        profesorGuardado.setNombre("Tomas");
        profesorGuardado.setApellido("Aguirrezabala");
        profesorGuardado.setTitulo("Tecnico Universitario en Programacion");
        
        List<Materia> materias = Arrays.asList(materia1);
        profesorGuardado.setMaterias(materias);
        
        when(profesorDao.guardar(any(Profesor.class))).thenReturn(profesorGuardado);

        Profesor resultado = profesorService.guardar(profesorDto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getMaterias().size());
        assertEquals(1L, resultado.getMaterias().get(0).getId());
        assertEquals("Base de Datos", resultado.getMaterias().get(0).getNombre());
        
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(999L);
        verify(profesorDao).guardar(any(Profesor.class));
    }


@Test
void buscarPorId_debeRetornarProfesor_cuandoExiste() {
    Long profesorId = 1L;
    
    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas");
    profesor.setApellido("Aguirrezabala");
    profesor.setTitulo("Tecnico Universitario en Programacion");

    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    Optional<Profesor> resultado = profesorService.buscarPorId(profesorId);

    assertTrue(resultado.isPresent());
    assertEquals(profesorId, resultado.get().getId());
    assertEquals("Tomas", resultado.get().getNombre());
    assertEquals("Aguirrezabala", resultado.get().getApellido());
    assertEquals("Tecnico Universitario en Programacion", resultado.get().getTitulo());
    
    verify(profesorDao).buscarPorId(profesorId);
}

@Test
void buscarPorId_debeRetornarOptionalVacio_cuandoNoExiste() {
    Long profesorId = 999L;

    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.empty());

    Optional<Profesor> resultado = profesorService.buscarPorId(profesorId);

    assertFalse(resultado.isPresent());
    
    verify(profesorDao).buscarPorId(profesorId);
}

@Test
void buscarTodos_debeRetornarListaProfesores_cuandoHayProfesores() {
    List<Profesor> profesores = new ArrayList<>();
    
    Profesor profesor1 = new Profesor();
    profesor1.setId(1L);
    profesor1.setNombre("Tomas");
    profesor1.setApellido("Aguirrezabala");
    profesor1.setTitulo("Tecnico Universitario en Programacion");
    
    Profesor profesor2 = new Profesor();
    profesor2.setId(2L);
    profesor2.setNombre("Tomas2");
    profesor2.setApellido("Aguirrezabala2");
    profesor2.setTitulo("Tecnico Universitario en Programacion II");
    
    profesores.add(profesor1);
    profesores.add(profesor2);

    when(profesorDao.buscarAll()).thenReturn(profesores);

    List<Profesor> resultado = profesorService.buscarTodos();

    assertNotNull(resultado);
    assertEquals(2, resultado.size());

    assertEquals(1L, resultado.get(0).getId());
    assertEquals("Tomas", resultado.get(0).getNombre());
    assertEquals("Aguirrezabala", resultado.get(0).getApellido());
    assertEquals("Tecnico Universitario en Programacion", resultado.get(0).getTitulo());

    assertEquals(2L, resultado.get(1).getId());
    assertEquals("Tomas2", resultado.get(1).getNombre());
    assertEquals("Aguirrezabala2", resultado.get(1).getApellido());
    assertEquals("Tecnico Universitario en Programacion II", resultado.get(1).getTitulo());
    
    verify(profesorDao).buscarAll();
}

@Test
void buscarTodos_debeRetornarListaVacia_cuandoNoHayProfesores() {
    List<Profesor> profesores = new ArrayList<>();

    when(profesorDao.buscarAll()).thenReturn(profesores);

    List<Profesor> resultado = profesorService.buscarTodos();

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    
    verify(profesorDao).buscarAll();
}

@Test
void eliminarPorId_debeEliminarProfesor_cuandoNoTieneMaterias() throws EntidadNoEncontradaException, ReglaNegocioException {
    Long profesorId = 1L;
    
    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas");
    profesor.setApellido("Aguirrezabala");
    profesor.setTitulo("Tecnico Universitario en Programacion");
    profesor.setMaterias(new ArrayList<>()); 
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    when(materiaDao.buscarAll()).thenReturn(new ArrayList<>());

    profesorService.eliminarPorId(profesorId);

    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao).buscarAll();
    verify(profesorDao).borrarPorId(profesorId);
}

@Test
void eliminarPorId_debeFallar_cuandoNoExisteProfesor() {
    Long profesorId = 999L;
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> profesorService.eliminarPorId(profesorId),
        "Debería lanzar excepción porque el profesor no existe"
    );
    
    assertTrue(exception.getMessage().contains("Profesor"));
    assertTrue(exception.getMessage().contains(profesorId.toString()));
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(profesorDao, never()).borrarPorId(anyLong());
}

@Test
void eliminarPorId_debeFallar_cuandoTieneMateriasDesdeLaPropiedad() {
    Long profesorId = 2L;

    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas2");
    profesor.setApellido("Aguirrezabala2");
    profesor.setTitulo("Tecnico Universitario en Programacion II");

    Materia materia = new Materia();
    materia.setId(1L);
    materia.setNombre("Programación I");
    
    profesor.setMaterias(Arrays.asList(materia));
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> profesorService.eliminarPorId(profesorId),
        "Debería lanzar excepción porque el profesor tiene materias asignadas"
    );
    
    assertTrue(exception.getMessage().contains("materias asignadas"));
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(profesorDao, never()).borrarPorId(anyLong());
}

@Test
void eliminarPorId_debeFallar_cuandoTieneMateriasDesdeBD() {

    Long profesorId = 3L;

    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas3");
    profesor.setApellido("Aguirrezabala3");
    profesor.setTitulo("Tecnico Universitario en Programacion III");
    profesor.setMaterias(new ArrayList<>()); 
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    Materia materia = new Materia();
    materia.setId(2L);
    materia.setNombre("Base de Datos");
    
    Profesor profesorReferencia = new Profesor();
    profesorReferencia.setId(profesorId);
    materia.setProfesor(profesorReferencia);
    
    when(materiaDao.buscarAll()).thenReturn(Arrays.asList(materia));

    ReglaNegocioException exception = assertThrows(
        ReglaNegocioException.class,
        () -> profesorService.eliminarPorId(profesorId),
        "Debería lanzar excepción porque el profesor tiene materias asignadas en la BD"
    );
    
    assertTrue(exception.getMessage().contains("dicta las siguientes materias"));
    assertTrue(exception.getMessage().contains("Base de Datos"));
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao).buscarAll();
    verify(profesorDao, never()).borrarPorId(anyLong());
}

@Test
void obtenerMateriasOrdenadas_debeRetornarMateriasOrdenadas_cuandoProfesorExiste() throws EntidadNoEncontradaException {

    Long profesorId = 1L;

    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas");
    profesor.setApellido("Aguirrezabala");
    profesor.setTitulo("Tecnico Universitario en Programacion");

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    Materia materia3 = new Materia();
    materia3.setId(3L);
    materia3.setNombre("Estadistica");

    profesor.setMaterias(Arrays.asList(materia1, materia3));

    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    Materia materia4 = new Materia();
    materia4.setId(4L);
    materia4.setNombre("Contabilidad");
    
    Profesor profesorReferencia = new Profesor();
    profesorReferencia.setId(profesorId);
    materia4.setProfesor(profesorReferencia);

    materia2.setProfesor(profesorReferencia);

    when(materiaDao.buscarAll()).thenReturn(Arrays.asList(materia1, materia2, materia3, materia4));

    List<Materia> resultado = profesorService.obtenerMateriasOrdenadas(profesorId);

    assertNotNull(resultado);
    assertEquals(4, resultado.size(), "Deberían haber 4 materias en total (2 del profesor + 2 de la BD)");

    assertEquals("Base de Datos", resultado.get(0).getNombre());
    assertEquals("Contabilidad", resultado.get(1).getNombre());
    assertEquals("Estadistica", resultado.get(2).getNombre());
    assertEquals("Programación I", resultado.get(3).getNombre());
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao).buscarAll();
}

@Test
void obtenerMateriasOrdenadas_debeRetornarListaVacia_cuandoProfesorSinMaterias() throws EntidadNoEncontradaException {

    Long profesorId = 2L;

    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas2");
    profesor.setApellido("Aguirrezabala2");
    profesor.setTitulo("Tecnico Universitario en Programacion II");
    profesor.setMaterias(new ArrayList<>());
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    List<Materia> materiasEnBD = new ArrayList<>();
    materiasEnBD.add(crearMateriaConProfesor(1L, "Programación I", 3L)); // otro profesor
    materiasEnBD.add(crearMateriaConProfesor(2L, "Base de Datos", 3L));   // otro profesor
    
    when(materiaDao.buscarAll()).thenReturn(materiasEnBD);

    List<Materia> resultado = profesorService.obtenerMateriasOrdenadas(profesorId);

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty(), "La lista debería estar vacía");
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao).buscarAll();
}

@Test
void obtenerMateriasOrdenadas_debeFallar_cuandoProfesorNoExiste() {

    Long profesorId = 999L;
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> profesorService.obtenerMateriasOrdenadas(profesorId),
        "Debería lanzar excepción porque el profesor no existe"
    );
    
    assertTrue(exception.getMessage().contains("Profesor"));
    assertTrue(exception.getMessage().contains(profesorId.toString()));
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao, never()).buscarAll();
}

@Test
void obtenerMateriasOrdenadas_noDebeDuplicarMaterias_cuandoExistenEnAmbosLados() throws EntidadNoEncontradaException {

    Long profesorId = 3L;

    Profesor profesor = new Profesor();
    profesor.setId(profesorId);
    profesor.setNombre("Tomas3");
    profesor.setApellido("Aguirrezabala3");
    profesor.setTitulo("Tecnico Universitario en Programacion III");

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");

    profesor.setMaterias(Arrays.asList(materia1, materia2));
    
    when(profesorDao.buscarPorId(profesorId)).thenReturn(Optional.of(profesor));

    Profesor profesorReferencia = new Profesor();
    profesorReferencia.setId(profesorId);
    materia1.setProfesor(profesorReferencia);
    materia2.setProfesor(profesorReferencia);
    
    when(materiaDao.buscarAll()).thenReturn(Arrays.asList(materia1, materia2));

    List<Materia> resultado = profesorService.obtenerMateriasOrdenadas(profesorId);

    assertNotNull(resultado);
    assertEquals(2, resultado.size(), "No debería haber duplicados, solo 2 materias");

    assertEquals("Base de Datos", resultado.get(0).getNombre());
    assertEquals("Programación I", resultado.get(1).getNombre());
    
    verify(profesorDao).buscarPorId(profesorId);
    verify(materiaDao).buscarAll();
}

private Materia crearMateriaConProfesor(Long materiaId, String nombreMateria, Long profesorId) {
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre(nombreMateria);
    
    if (profesorId != null) {
        Profesor profesor = new Profesor();
        profesor.setId(profesorId);
        materia.setProfesor(profesor);
    }
    
    return materia;
}
}