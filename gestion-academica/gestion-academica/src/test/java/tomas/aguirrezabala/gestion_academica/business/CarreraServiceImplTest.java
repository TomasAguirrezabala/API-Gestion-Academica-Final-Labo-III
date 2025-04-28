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

import tomas.aguirrezabala.gestion_academica.business.impl.CarreraServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.CarreraDto;
import tomas.aguirrezabala.gestion_academica.persistence.CarreraDao;
import tomas.aguirrezabala.gestion_academica.persistence.MateriaDao;

public class CarreraServiceImplTest {
    
    @Mock
    private CarreraDao carreraDao;
    
    @Mock
    private MateriaDao materiaDao;
    
    @InjectMocks
    private CarreraServiceImpl carreraService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void guardar_debeCrearCarrera_cuandoDatosValidos() throws EntidadDuplicadaException {
        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setNombre("Tecnicatura Universitaria en Programación");
        carreraDto.setDuracionAnios(6); 

        when(carreraDao.buscarAll()).thenReturn(new ArrayList<>());
        
        Carrera carreraCreada = new Carrera();
        carreraCreada.setId(1L);
        carreraCreada.setNombre("Tecnicatura Universitaria en Programación");
        carreraCreada.setDuracionAnios(6);
        
        when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraCreada);
        

        Carrera resultado = carreraService.guardar(carreraDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tecnicatura Universitaria en Programación", resultado.getNombre());
        assertEquals(6, resultado.getDuracionAnios());
        
        verify(carreraDao).buscarAll();
        verify(carreraDao).guardar(any(Carrera.class));
    }
    
    @Test
    void guardar_debeRechazarCreacion_cuandoNombreDuplicado() {

        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setNombre("Tecnicatura Universitaria en Programación");
        carreraDto.setDuracionAnios(6);

        Carrera carreraExistente = new Carrera();
        carreraExistente.setId(1L);
        carreraExistente.setNombre("Tecnicatura Universitaria en Programación");
        carreraExistente.setDuracionAnios(8);

        List<Carrera> carrerasExistentes = Arrays.asList(carreraExistente);
        when(carreraDao.buscarAll()).thenReturn(carrerasExistentes);

        EntidadDuplicadaException exception = assertThrows(
            EntidadDuplicadaException.class,
            () -> carreraService.guardar(carreraDto),
            "Debería lanzar excepción por nombre duplicado"
        );

        assertTrue(exception.getMessage().contains("nombre"));
        assertTrue(exception.getMessage().contains("Tecnicatura Universitaria en Programación"));

        verify(carreraDao).buscarAll();
        verify(carreraDao, never()).guardar(any(Carrera.class));
    }
    
    @Test
    void guardar_debeCrearCarreraConMaterias_cuandoMateriasExisten() throws EntidadDuplicadaException {

        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setNombre("Tecnicatura Universitaria en Programación");
        carreraDto.setDuracionAnios(6);
        
        List<Long> materiasIds = Arrays.asList(1L, 2L);
        carreraDto.setMateriasIds(materiasIds);

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");

        when(carreraDao.buscarAll()).thenReturn(new ArrayList<>());
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(materia1));
        when(materiaDao.buscarPorId(2L)).thenReturn(Optional.of(materia2));
        
        Carrera carreraCreada = new Carrera();
        carreraCreada.setId(1L);
        carreraCreada.setNombre("Tecnicatura Universitaria en Programación");
        carreraCreada.setDuracionAnios(6);
        carreraCreada.setMaterias(Arrays.asList(materia1, materia2));
        
        when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraCreada);

        Carrera resultado = carreraService.guardar(carreraDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tecnicatura Universitaria en Programación", resultado.getNombre());
        assertEquals(2, resultado.getMaterias().size());
        assertEquals("Programación I", resultado.getMaterias().get(0).getNombre());
        assertEquals("Base de Datos", resultado.getMaterias().get(1).getNombre());
        
        verify(carreraDao).buscarAll();
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(2L);
        verify(carreraDao).guardar(any(Carrera.class));
    }
    
    @Test
    void guardar_debeIgnorarMateriasNoExistentes() throws EntidadDuplicadaException {

        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setNombre("Tecnicatura Universitaria en Programación");
        carreraDto.setDuracionAnios(6);
        
        List<Long> materiasIds = Arrays.asList(1L, 999L);
        carreraDto.setMateriasIds(materiasIds);

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");

        when(carreraDao.buscarAll()).thenReturn(new ArrayList<>());
        when(materiaDao.buscarPorId(1L)).thenReturn(Optional.of(materia1));
        when(materiaDao.buscarPorId(999L)).thenReturn(Optional.empty());
        
        Carrera carreraCreada = new Carrera();
        carreraCreada.setId(1L);
        carreraCreada.setNombre("Tecnicatura Universitaria en Programación");
        carreraCreada.setDuracionAnios(6);
        carreraCreada.setMaterias(Arrays.asList(materia1)); 
        
        when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraCreada);

        Carrera resultado = carreraService.guardar(carreraDto);

        assertNotNull(resultado);
        assertEquals(1, resultado.getMaterias().size(), "Solo debería tener una materia válida");
        assertEquals("Programación I", resultado.getMaterias().get(0).getNombre());
        
        verify(carreraDao).buscarAll();
        verify(materiaDao).buscarPorId(1L);
        verify(materiaDao).buscarPorId(999L);
        verify(carreraDao).guardar(any(Carrera.class));
    }
    
    @Test
    void guardar_debeActualizarCarrera_cuandoTieneId() throws EntidadDuplicadaException {

        Long carreraId = 1L;
        
        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setId(carreraId);
        carreraDto.setNombre("Tecnicatura Universitaria en Programación Actualizada");
        carreraDto.setDuracionAnios(8); 
        
        Carrera carreraActualizada = new Carrera();
        carreraActualizada.setId(carreraId);
        carreraActualizada.setNombre("Tecnicatura Universitaria en Programación Actualizada");
        carreraActualizada.setDuracionAnios(8);
        
        when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraActualizada);

        Carrera resultado = carreraService.guardar(carreraDto);

        assertNotNull(resultado);
        assertEquals(carreraId, resultado.getId());
        assertEquals("Tecnicatura Universitaria en Programación Actualizada", resultado.getNombre());
        assertEquals(8, resultado.getDuracionAnios());

        verify(carreraDao, never()).buscarAll();
        verify(carreraDao).guardar(any(Carrera.class));
    }
    
    @Test
    void guardar_debeCrearCarreraConListaMateriasVacia_cuandoNoSeEspecificanMaterias() throws EntidadDuplicadaException {

        CarreraDto carreraDto = new CarreraDto();
        carreraDto.setNombre("Tecnicatura Universitaria en Programación");
        carreraDto.setDuracionAnios(6);

        
        when(carreraDao.buscarAll()).thenReturn(new ArrayList<>());
        
        Carrera carreraCreada = new Carrera();
        carreraCreada.setId(1L);
        carreraCreada.setNombre("Tecnicatura Universitaria en Programación");
        carreraCreada.setDuracionAnios(6);
        carreraCreada.setMaterias(new ArrayList<>());
        
        when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraCreada);

        Carrera resultado = carreraService.guardar(carreraDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tecnicatura Universitaria en Programación", resultado.getNombre());
        assertTrue(resultado.getMaterias().isEmpty(), "La lista de materias debería estar vacía");
        
        verify(carreraDao).buscarAll();
        verify(carreraDao).guardar(any(Carrera.class));
        verify(materiaDao, never()).buscarPorId(anyLong()); 
    }


@Test
void buscarPorId_debeRetornarCarrera_cuandoExiste() {

    Long carreraId = 1L;
    
    Carrera carreraSimulada = new Carrera();
    carreraSimulada.setId(carreraId);
    carreraSimulada.setNombre("Tecnicatura Universitaria en Programación");
    carreraSimulada.setDuracionAnios(6);

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    carreraSimulada.setMaterias(Arrays.asList(materia1, materia2));
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.of(carreraSimulada));

    Optional<Carrera> resultado = carreraService.buscarPorId(carreraId);

    assertTrue(resultado.isPresent(), "El resultado debería contener una carrera");
    assertEquals(carreraId, resultado.get().getId(), "El ID de la carrera debe ser correcto");
    assertEquals("Tecnicatura Universitaria en Programación", resultado.get().getNombre(), 
            "El nombre de la carrera debe ser correcto");
    assertEquals(6, resultado.get().getDuracionAnios(), 
            "La duración de la carrera debe ser correcta");
    assertEquals(2, resultado.get().getMaterias().size(), 
            "La carrera debe tener 2 materias asociadas");
    
    verify(carreraDao).buscarPorId(carreraId);
}

@Test
void buscarPorId_debeRetornarOptionalVacio_cuandoNoExiste() {

    Long carreraId = 999L;
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.empty());

    Optional<Carrera> resultado = carreraService.buscarPorId(carreraId);

    assertFalse(resultado.isPresent(), "El resultado debería estar vacío");
    
    verify(carreraDao).buscarPorId(carreraId);
}

@Test
void buscarTodas_debeRetornarListaDeCarreras_cuandoHayCarreras() {

    List<Carrera> carrerasSimuladas = new ArrayList<>();

    Carrera carrera1 = new Carrera();
    carrera1.setId(1L);
    carrera1.setNombre("Tecnicatura Universitaria en Programación");
    carrera1.setDuracionAnios(6);

    Carrera carrera2 = new Carrera();
    carrera2.setId(2L);
    carrera2.setNombre("Ingeniería en Sistemas");
    carrera2.setDuracionAnios(10);
    
    carrerasSimuladas.add(carrera1);
    carrerasSimuladas.add(carrera2);
    
    when(carreraDao.buscarAll()).thenReturn(carrerasSimuladas);

    List<Carrera> resultado = carreraService.buscarTodas();

    assertNotNull(resultado, "El resultado no debería ser null");
    assertEquals(2, resultado.size(), "Deberían haber 2 carreras");
    
    assertEquals(1L, resultado.get(0).getId(), "El ID de la primera carrera debe ser correcto");
    assertEquals("Tecnicatura Universitaria en Programación", resultado.get(0).getNombre(), 
            "El nombre de la primera carrera debe ser correcto");
    assertEquals(6, resultado.get(0).getDuracionAnios(), 
            "La duración de la primera carrera debe ser correcta");
    
    assertEquals(2L, resultado.get(1).getId(), "El ID de la segunda carrera debe ser correcto");
    assertEquals("Ingeniería en Sistemas", resultado.get(1).getNombre(), 
            "El nombre de la segunda carrera debe ser correcto");
    assertEquals(10, resultado.get(1).getDuracionAnios(), 
            "La duración de la segunda carrera debe ser correcta");
    
    verify(carreraDao).buscarAll();
}

@Test
void buscarTodas_debeRetornarListaVacia_cuandoNoHayCarreras() {

    when(carreraDao.buscarAll()).thenReturn(new ArrayList<>());

    List<Carrera> resultado = carreraService.buscarTodas();

    assertNotNull(resultado, "El resultado no debería ser null, sino una lista vacía");
    assertTrue(resultado.isEmpty(), "La lista debería estar vacía");
    
    verify(carreraDao).buscarAll();
}

@Test
void eliminarPorId_debeEliminarCarrera_cuandoExiste() throws EntidadNoEncontradaException {

    Long carreraId = 1L;

    Carrera carrera = new Carrera();
    carrera.setId(carreraId);
    carrera.setNombre("Tecnicatura Universitaria en Programación");
    carrera.setDuracionAnios(6);
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));

    carreraService.eliminarPorId(carreraId);

    verify(carreraDao).buscarPorId(carreraId);
    verify(carreraDao).borrarPorId(carreraId);
}

@Test
void eliminarPorId_debeFallar_cuandoNoExiste() {

    Long carreraId = 999L;
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> carreraService.eliminarPorId(carreraId),
        "Debería lanzar excepción cuando la carrera no existe"
    );
    
    assertTrue(exception.getMessage().contains("Carrera"));
    assertTrue(exception.getMessage().contains(carreraId.toString()));
    
    verify(carreraDao).buscarPorId(carreraId);
    verify(carreraDao, never()).borrarPorId(anyLong());
}

@Test
void agregarMateria_debeAgregarMateriaACarrera_cuandoAmbosExistenYNoEstaDuplicada() 
        throws EntidadNoEncontradaException, EntidadDuplicadaException {

    Long carreraId = 1L;
    Long materiaId = 3L;

    Carrera carrera = new Carrera();
    carrera.setId(carreraId);
    carrera.setNombre("Tecnicatura Universitaria en Programación");
    carrera.setDuracionAnios(6);

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    carrera.setMaterias(new ArrayList<>(Arrays.asList(materia1, materia2)));

    Materia materia3 = new Materia();
    materia3.setId(materiaId);
    materia3.setNombre("Estadistica");

    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia3));

    Carrera carreraActualizada = new Carrera();
    carreraActualizada.setId(carreraId);
    carreraActualizada.setNombre("Tecnicatura Universitaria en Programación");
    carreraActualizada.setDuracionAnios(6);
    carreraActualizada.setMaterias(Arrays.asList(materia1, materia2, materia3));
    
    when(carreraDao.guardar(any(Carrera.class))).thenReturn(carreraActualizada);

    Carrera resultado = carreraService.agregarMateria(carreraId, materiaId);

    assertNotNull(resultado);
    assertEquals(carreraId, resultado.getId());
    assertEquals(3, resultado.getMaterias().size(), "La carrera debería tener 3 materias");
    assertEquals("Estadistica", resultado.getMaterias().get(2).getNombre());
    
    verify(carreraDao).buscarPorId(carreraId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(carreraDao).guardar(any(Carrera.class));
}

@Test
void agregarMateria_debeFallar_cuandoCarreraNoExiste() {

    Long carreraId = 999L;
    Long materiaId = 1L;
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> carreraService.agregarMateria(carreraId, materiaId),
        "Debería lanzar excepción cuando la carrera no existe"
    );
    
    assertTrue(exception.getMessage().contains("Carrera"));
    assertTrue(exception.getMessage().contains(carreraId.toString()));
    
    verify(carreraDao).buscarPorId(carreraId);
    verify(materiaDao, never()).buscarPorId(anyLong());
    verify(carreraDao, never()).guardar(any(Carrera.class));
}

@Test
void agregarMateria_debeFallar_cuandoMateriaNoExiste() {

    Long carreraId = 1L;
    Long materiaId = 999L;

    Carrera carrera = new Carrera();
    carrera.setId(carreraId);
    carrera.setNombre("Tecnicatura Universitaria en Programación");
    carrera.setDuracionAnios(6);
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.empty());

    EntidadNoEncontradaException exception = assertThrows(
        EntidadNoEncontradaException.class,
        () -> carreraService.agregarMateria(carreraId, materiaId),
        "Debería lanzar excepción cuando la materia no existe"
    );
    
    assertTrue(exception.getMessage().contains("Materia"));
    assertTrue(exception.getMessage().contains(materiaId.toString()));
    
    verify(carreraDao).buscarPorId(carreraId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(carreraDao, never()).guardar(any(Carrera.class));
}

@Test
void agregarMateria_debeFallar_cuandoMateriaYaEstaAsignada() {

    Long carreraId = 1L;
    Long materiaId = 1L;

    Carrera carrera = new Carrera();
    carrera.setId(carreraId);
    carrera.setNombre("Tecnicatura Universitaria en Programación");
    carrera.setDuracionAnios(6);
    
    Materia materia = new Materia();
    materia.setId(materiaId);
    materia.setNombre("Programación I");
    
    carrera.setMaterias(Arrays.asList(materia));
    
    when(carreraDao.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));
    when(materiaDao.buscarPorId(materiaId)).thenReturn(Optional.of(materia));

    EntidadDuplicadaException exception = assertThrows(
        EntidadDuplicadaException.class,
        () -> carreraService.agregarMateria(carreraId, materiaId),
        "Debería lanzar excepción cuando la materia ya está asignada a la carrera"
    );
    
    assertTrue(exception.getMessage().contains("Materia"));
    assertTrue(exception.getMessage().contains("ya está asignada"));
    
    verify(carreraDao).buscarPorId(carreraId);
    verify(materiaDao).buscarPorId(materiaId);
    verify(carreraDao, never()).guardar(any(Carrera.class));
}
}