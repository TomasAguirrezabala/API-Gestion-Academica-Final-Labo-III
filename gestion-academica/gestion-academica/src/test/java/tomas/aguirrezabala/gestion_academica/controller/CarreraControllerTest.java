package tomas.aguirrezabala.gestion_academica.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import tomas.aguirrezabala.gestion_academica.business.CarreraService;
import tomas.aguirrezabala.gestion_academica.controller.handler.CustomResponseEntityExceptionHandler;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.CarreraDto;

public class CarreraControllerTest {

    @Mock
    private CarreraService carreraService;

    @InjectMocks
    private CarreraController carreraController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar MockMvc con el controlador y el manejador de excepciones personalizado
        mockMvc = MockMvcBuilders.standaloneSetup(carreraController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }

    @Test
    void listarTodas_debeRetornarListaDeCarreras_cuandoHayCarreras() throws Exception {
        // Preparación
        Carrera carrera1 = new Carrera(1L, "Ingeniería Informática", 5);
        Carrera carrera2 = new Carrera(2L, "Licenciatura en Sistemas", 4);
        
        List<Carrera> carreras = Arrays.asList(carrera1, carrera2);
        
        // Mock del servicio
        when(carreraService.buscarTodas()).thenReturn(carreras);
        
        // Ejecución y verificación
        mockMvc.perform(get("/carrera")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Ingeniería Informática")))
                .andExpect(jsonPath("$[0].duracionAnios", is(5)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Licenciatura en Sistemas")))
                .andExpect(jsonPath("$[1].duracionAnios", is(4)));
        
        // Verificar que el servicio fue llamado
        verify(carreraService, times(1)).buscarTodas();
    }
    
    @Test
    void listarTodas_debeRetornarListaVacia_cuandoNoHayCarreras() throws Exception {
        // Mock del servicio
        when(carreraService.buscarTodas()).thenReturn(Collections.emptyList());
        
        // Ejecución y verificación
        mockMvc.perform(get("/carrera")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        // Verificar que el servicio fue llamado
        verify(carreraService, times(1)).buscarTodas();
    }
    
    @Test
    void buscarPorId_debeRetornarCarrera_cuandoExiste() throws Exception {
        // Preparación
        Long idBuscado = 1L;
        
        Carrera carrera = new Carrera(idBuscado, "Ingeniería Informática", 5);
        
        // Agregar algunas materias a la carrera
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        carrera.setMaterias(Arrays.asList(materia1, materia2));
        
        // Mock del servicio
        when(carreraService.buscarPorId(idBuscado)).thenReturn(Optional.of(carrera));
        
        // Ejecución y verificación
        mockMvc.perform(get("/carrera/{id}", idBuscado)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Ingeniería Informática")))
                .andExpect(jsonPath("$.duracionAnios", is(5)))
                .andExpect(jsonPath("$.materias", hasSize(2)))
                .andExpect(jsonPath("$.materias[0].id", is(1)))
                .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
                .andExpect(jsonPath("$.materias[1].id", is(2)))
                .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")));
        
        // Verificar que el servicio fue llamado
        verify(carreraService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio
        when(carreraService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(get("/carrera/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(carreraService, times(1)).buscarPorId(idInexistente);
    }

    @Test
    void crear_debeRetornarCarreraCreada_cuandoDatosValidos() throws Exception {
    // Preparación
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Software");
    carreraDto.setDuracionAnios(10); // Cantidad de cuatrimestres (5 años)
    carreraDto.setMateriasIds(Arrays.asList(1L, 2L));
    
    Carrera carreraCreada = new Carrera(1L, "Ingeniería en Software", 10);
    
    // Agregar materias a la carrera creada
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    carreraCreada.setMaterias(Arrays.asList(materia1, materia2));
    
    // Mock del servicio
    when(carreraService.guardar(any(CarreraDto.class))).thenReturn(carreraCreada);
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Ingeniería en Software")))
            .andExpect(jsonPath("$.duracionAnios", is(10)))
            .andExpect(jsonPath("$.materias", hasSize(2)))
            .andExpect(jsonPath("$.materias[0].id", is(1)))
            .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
            .andExpect(jsonPath("$.materias[1].id", is(2)))
            .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {
    // Preparación
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Informática"); // Nombre duplicado
    carreraDto.setDuracionAnios(10);
    
    // Mock del servicio - simula un error de nombre duplicado
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadDuplicadaException("Carrera", "nombre", "Ingeniería Informática"));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
    // Preparación
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Robótica");
    carreraDto.setDuracionAnios(10);
    carreraDto.setMateriasIds(Arrays.asList(1L, 999L)); // ID 999 no existe
    
    // Mock del servicio - simula un error de materia no encontrada
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Materia", 999L));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoDatosInvalidos() throws Exception {
    // Preparación - Carrera sin nombre
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setDuracionAnios(10);
    
    // Mock del servicio - simula un error de validación
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new IllegalArgumentException("El nombre de la carrera es obligatorio"));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status", is(500)))
            .andExpect(jsonPath("$.error", is("Internal Server Error")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeRetornarCarreraActualizada_cuandoDatosValidos() throws Exception {
    // Preparación
    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Software Actualizada");
    carreraDto.setDuracionAnios(8);
    carreraDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));
    
    Carrera carreraActualizada = new Carrera(idActualizar, "Ingeniería en Software Actualizada", 8);
    
    // Agregar materias a la carrera actualizada
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    Materia materia3 = new Materia();
    materia3.setId(3L);
    materia3.setNombre("Sistemas Operativos");
    
    carreraActualizada.setMaterias(Arrays.asList(materia1, materia2, materia3));
    
    // Mock del servicio - verificamos que se llama con el ID establecido en el DTO
    when(carreraService.guardar(any(CarreraDto.class))).thenAnswer(invocation -> {
        CarreraDto dto = invocation.getArgument(0);
        assertEquals(idActualizar, dto.getId(), "El ID en el DTO debe ser establecido al valor del path variable");
        return carreraActualizada;
    });
    
    // Ejecución y verificación
    mockMvc.perform(put("/carrera/{id}", idActualizar)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Ingeniería en Software Actualizada")))
            .andExpect(jsonPath("$.duracionAnios", is(8)))
            .andExpect(jsonPath("$.materias", hasSize(3)))
            .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
            .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")))
            .andExpect(jsonPath("$.materias[2].nombre", is("Sistemas Operativos")));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
    // Preparación
    Long idInexistente = 999L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Actualizada");
    carreraDto.setDuracionAnios(8);
    
    // Mock del servicio - simula un error de carrera no encontrada
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Carrera", idInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(put("/carrera/{id}", idInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {
    // Preparación
    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Licenciatura en Sistemas"); // Nombre duplicado con otra carrera
    carreraDto.setDuracionAnios(8);
    
    // Mock del servicio - simula un error de nombre duplicado
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadDuplicadaException("Carrera", "nombre", "Licenciatura en Sistemas"));
    
    // Ejecución y verificación
    mockMvc.perform(put("/carrera/{id}", idActualizar)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
    // Preparación
    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Actualizada");
    carreraDto.setDuracionAnios(8);
    carreraDto.setMateriasIds(Arrays.asList(1L, 999L)); // ID 999 no existe
    
    // Mock del servicio - simula un error de materia no encontrada
    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Materia", 999L));
    
    // Ejecución y verificación
    mockMvc.perform(put("/carrera/{id}", idActualizar)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {
    // Preparación
    Long idEliminar = 1L;
    
    // Mock del servicio - configurar para que no haga nada (éxito)
    doNothing().when(carreraService).eliminarPorId(idEliminar);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/carrera/{id}", idEliminar)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).eliminarPorId(idEliminar);
}

    @Test
    void eliminar_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
    // Preparación
    Long idInexistente = 999L;
    
    // Mock del servicio - simula un error de carrera no encontrada
    doThrow(new EntidadNoEncontradaException("Carrera", idInexistente))
        .when(carreraService).eliminarPorId(idInexistente);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/carrera/{id}", idInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).eliminarPorId(idInexistente);
}

    @Test
    void eliminar_debeLanzarExcepcion_cuandoCarreraEstaEnUso() throws Exception {
    // Preparación
    Long idEnUso = 1L;
    
    // Mock del servicio - simula un error de carrera en uso por alumnos
    doThrow(new ReglaNegocioException("No se puede eliminar la carrera porque tiene alumnos asociados"))
        .when(carreraService).eliminarPorId(idEnUso);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/carrera/{id}", idEnUso)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la carrera porque tiene alumnos asociados"));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).eliminarPorId(idEnUso);
}

    @Test
    void agregarMateria_debeRetornarCarreraActualizada_cuandoDatosValidos() throws Exception {
    // Preparación
    Long carreraId = 1L;
    Long materiaId = 3L;
    
    // Crear carrera simulada
    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);
    
    // Materias existentes
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    // Nueva materia a agregar
    Materia materia3 = new Materia();
    materia3.setId(materiaId);
    materia3.setNombre("Sistemas Operativos");
    
    // Establecer materias iniciales
    carrera.setMaterias(Arrays.asList(materia1, materia2));
    
    // Carrera después de agregar la materia
    Carrera carreraActualizada = new Carrera(carreraId, "Ingeniería Informática", 5);
    carreraActualizada.setMaterias(Arrays.asList(materia1, materia2, materia3));
    
    // Mock del servicio
    when(carreraService.agregarMateria(carreraId, materiaId)).thenReturn(carreraActualizada);
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraId, materiaId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.nombre", is("Ingeniería Informática")))
            .andExpect(jsonPath("$.materias", hasSize(3)))
            .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
            .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")))
            .andExpect(jsonPath("$.materias[2].nombre", is("Sistemas Operativos")))
            .andExpect(jsonPath("$.materias[2].id", is(3)));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).agregarMateria(carreraId, materiaId);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
    // Preparación
    Long carreraIdInexistente = 999L;
    Long materiaId = 1L;
    
    // Mock del servicio - simula error cuando la carrera no existe
    when(carreraService.agregarMateria(carreraIdInexistente, materiaId))
        .thenThrow(new EntidadNoEncontradaException("Carrera", carreraIdInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraIdInexistente, materiaId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).agregarMateria(carreraIdInexistente, materiaId);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
    // Preparación
    Long carreraId = 1L;
    Long materiaIdInexistente = 999L;
    
    // Mock del servicio - simula error cuando la materia no existe
    when(carreraService.agregarMateria(carreraId, materiaIdInexistente))
        .thenThrow(new EntidadNoEncontradaException("Materia", materiaIdInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraId, materiaIdInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).agregarMateria(carreraId, materiaIdInexistente);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoMateriaYaAsignada() throws Exception {
    // Preparación
    Long carreraId = 1L;
    Long materiaId = 2L; // Ya está asignada a la carrera
    
    // Mock del servicio - simula error de materia ya asignada
    when(carreraService.agregarMateria(carreraId, materiaId))
        .thenThrow(new EntidadDuplicadaException("Materia", "id", materiaId.toString() + " ya está asignada a la carrera"));
    
    // Ejecución y verificación
    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraId, materiaId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).agregarMateria(carreraId, materiaId);
}

    @Test
    void obtenerMaterias_debeRetornarListaDeMaterias_cuandoCarreraExiste() throws Exception {
    // Preparación
    Long carreraId = 1L;
    
    // Crear carrera simulada
    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);
    
    // Agregar materias a la carrera
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    materia1.setAnio(1);
    materia1.setCuatrimestre(1);
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    materia2.setAnio(2);
    materia2.setCuatrimestre(1);
    
    Materia materia3 = new Materia();
    materia3.setId(3L);
    materia3.setNombre("Sistemas Operativos");
    materia3.setAnio(2);
    materia3.setCuatrimestre(2);
    
    carrera.setMaterias(Arrays.asList(materia1, materia2, materia3));
    
    // Mock del servicio
    when(carreraService.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));
    
    // Ejecución y verificación
    mockMvc.perform(get("/carrera/{carreraId}/materias", carreraId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].nombre", is("Programación I")))
            .andExpect(jsonPath("$[0].anio", is(1)))
            .andExpect(jsonPath("$[0].cuatrimestre", is(1)))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].nombre", is("Base de Datos")))
            .andExpect(jsonPath("$[2].id", is(3)))
            .andExpect(jsonPath("$[2].nombre", is("Sistemas Operativos")));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).buscarPorId(carreraId);
}
    
    @Test
    void obtenerMaterias_debeRetornarListaVacia_cuandoCarreraNoTieneMaterias() throws Exception {
    // Preparación
    Long carreraId = 1L;
    
    // Crear carrera simulada sin materias
    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);
    carrera.setMaterias(new ArrayList<>());
    
    // Mock del servicio
    when(carreraService.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));
    
    // Ejecución y verificación
    mockMvc.perform(get("/carrera/{carreraId}/materias", carreraId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).buscarPorId(carreraId);
}

    @Test
    void obtenerMaterias_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
    // Preparación
    Long carreraIdInexistente = 999L;
    
    // Mock del servicio
    when(carreraService.buscarPorId(carreraIdInexistente)).thenReturn(Optional.empty());
    
    // Ejecución y verificación
    mockMvc.perform(get("/carrera/{carreraId}/materias", carreraIdInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(carreraService, times(1)).buscarPorId(carreraIdInexistente);
}

}