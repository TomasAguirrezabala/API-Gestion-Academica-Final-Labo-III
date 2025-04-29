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

        mockMvc = MockMvcBuilders.standaloneSetup(carreraController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }

    @Test
    void listarTodas_debeRetornarListaDeCarreras_cuandoHayCarreras() throws Exception {

        Carrera carrera1 = new Carrera(1L, "Ingeniería Informática", 5);
        Carrera carrera2 = new Carrera(2L, "Licenciatura en Sistemas", 4);
        
        List<Carrera> carreras = Arrays.asList(carrera1, carrera2);

        when(carreraService.buscarTodas()).thenReturn(carreras);

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

        verify(carreraService, times(1)).buscarTodas();
    }
    
    @Test
    void listarTodas_debeRetornarListaVacia_cuandoNoHayCarreras() throws Exception {

        when(carreraService.buscarTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/carrera")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(carreraService, times(1)).buscarTodas();
    }
    
    @Test
    void buscarPorId_debeRetornarCarrera_cuandoExiste() throws Exception {

        Long idBuscado = 1L;
        
        Carrera carrera = new Carrera(idBuscado, "Ingeniería Informática", 5);

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        carrera.setMaterias(Arrays.asList(materia1, materia2));

        when(carreraService.buscarPorId(idBuscado)).thenReturn(Optional.of(carrera));

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

        verify(carreraService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {

        Long idInexistente = 999L;

        when(carreraService.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carrera/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(carreraService, times(1)).buscarPorId(idInexistente);
    }

    @Test
    void crear_debeRetornarCarreraCreada_cuandoDatosValidos() throws Exception {

    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Software");
    carreraDto.setDuracionAnios(10); 
    carreraDto.setMateriasIds(Arrays.asList(1L, 2L));
    
    Carrera carreraCreada = new Carrera(1L, "Ingeniería en Software", 10);

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");
    
    carreraCreada.setMaterias(Arrays.asList(materia1, materia2));

    when(carreraService.guardar(any(CarreraDto.class))).thenReturn(carreraCreada);

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

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {

    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Informática"); 
    carreraDto.setDuracionAnios(10);

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadDuplicadaException("Carrera", "nombre", "Ingeniería Informática"));

    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Robótica");
    carreraDto.setDuracionAnios(10);
    carreraDto.setMateriasIds(Arrays.asList(1L, 999L));

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Materia", 999L));

    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void crear_debeLanzarExcepcion_cuandoDatosInvalidos() throws Exception {

    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setDuracionAnios(10);

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new IllegalArgumentException("El nombre de la carrera es obligatorio"));

    mockMvc.perform(post("/carrera")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status", is(500)))
            .andExpect(jsonPath("$.error", is("Internal Server Error")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeRetornarCarreraActualizada_cuandoDatosValidos() throws Exception {

    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería en Software Actualizada");
    carreraDto.setDuracionAnios(8);
    carreraDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));
    
    Carrera carreraActualizada = new Carrera(idActualizar, "Ingeniería en Software Actualizada", 8);

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

    when(carreraService.guardar(any(CarreraDto.class))).thenAnswer(invocation -> {
        CarreraDto dto = invocation.getArgument(0);
        assertEquals(idActualizar, dto.getId(), "El ID en el DTO debe ser establecido al valor del path variable");
        return carreraActualizada;
    });

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

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {

    Long idInexistente = 999L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Actualizada");
    carreraDto.setDuracionAnios(8);

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Carrera", idInexistente));

    mockMvc.perform(put("/carrera/{id}", idInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {

    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Licenciatura en Sistemas");
    carreraDto.setDuracionAnios(8);

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadDuplicadaException("Carrera", "nombre", "Licenciatura en Sistemas"));

    mockMvc.perform(put("/carrera/{id}", idActualizar)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void actualizar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

    Long idActualizar = 1L;
    
    CarreraDto carreraDto = new CarreraDto();
    carreraDto.setNombre("Ingeniería Actualizada");
    carreraDto.setDuracionAnios(8);
    carreraDto.setMateriasIds(Arrays.asList(1L, 999L)); 

    when(carreraService.guardar(any(CarreraDto.class)))
        .thenThrow(new EntidadNoEncontradaException("Materia", 999L));

    mockMvc.perform(put("/carrera/{id}", idActualizar)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carreraDto)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).guardar(any(CarreraDto.class));
}

    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {

    Long idEliminar = 1L;

    doNothing().when(carreraService).eliminarPorId(idEliminar);

    mockMvc.perform(delete("/carrera/{id}", idEliminar)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

    verify(carreraService, times(1)).eliminarPorId(idEliminar);
}

    @Test
    void eliminar_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {

    Long idInexistente = 999L;

    doThrow(new EntidadNoEncontradaException("Carrera", idInexistente))
        .when(carreraService).eliminarPorId(idInexistente);

    mockMvc.perform(delete("/carrera/{id}", idInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).eliminarPorId(idInexistente);
}

    @Test
    void eliminar_debeLanzarExcepcion_cuandoCarreraEstaEnUso() throws Exception {

    Long idEnUso = 1L;

    doThrow(new ReglaNegocioException("No se puede eliminar la carrera porque tiene alumnos asociados"))
        .when(carreraService).eliminarPorId(idEnUso);

    mockMvc.perform(delete("/carrera/{id}", idEnUso)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la carrera porque tiene alumnos asociados"));

    verify(carreraService, times(1)).eliminarPorId(idEnUso);
}

    @Test
    void agregarMateria_debeRetornarCarreraActualizada_cuandoDatosValidos() throws Exception {

    Long carreraId = 1L;
    Long materiaId = 3L;

    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);

    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Base de Datos");

    Materia materia3 = new Materia();
    materia3.setId(materiaId);
    materia3.setNombre("Sistemas Operativos");

    carrera.setMaterias(Arrays.asList(materia1, materia2));

    Carrera carreraActualizada = new Carrera(carreraId, "Ingeniería Informática", 5);
    carreraActualizada.setMaterias(Arrays.asList(materia1, materia2, materia3));

    when(carreraService.agregarMateria(carreraId, materiaId)).thenReturn(carreraActualizada);

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

    verify(carreraService, times(1)).agregarMateria(carreraId, materiaId);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {

    Long carreraIdInexistente = 999L;
    Long materiaId = 1L;

    when(carreraService.agregarMateria(carreraIdInexistente, materiaId))
        .thenThrow(new EntidadNoEncontradaException("Carrera", carreraIdInexistente));

    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraIdInexistente, materiaId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).agregarMateria(carreraIdInexistente, materiaId);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

    Long carreraId = 1L;
    Long materiaIdInexistente = 999L;

    when(carreraService.agregarMateria(carreraId, materiaIdInexistente))
        .thenThrow(new EntidadNoEncontradaException("Materia", materiaIdInexistente));

    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraId, materiaIdInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).agregarMateria(carreraId, materiaIdInexistente);
}

    @Test
    void agregarMateria_debeLanzarExcepcion_cuandoMateriaYaAsignada() throws Exception {

    Long carreraId = 1L;
    Long materiaId = 2L; 

    when(carreraService.agregarMateria(carreraId, materiaId))
        .thenThrow(new EntidadDuplicadaException("Materia", "id", materiaId.toString() + " ya está asignada a la carrera"));

    mockMvc.perform(post("/carrera/{carreraId}/materia/{materiaId}", carreraId, materiaId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).agregarMateria(carreraId, materiaId);
}

    @Test
    void obtenerMaterias_debeRetornarListaDeMaterias_cuandoCarreraExiste() throws Exception {

    Long carreraId = 1L;

    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);

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

    when(carreraService.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));

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

    verify(carreraService, times(1)).buscarPorId(carreraId);
}
    
    @Test
    void obtenerMaterias_debeRetornarListaVacia_cuandoCarreraNoTieneMaterias() throws Exception {

    Long carreraId = 1L;

    Carrera carrera = new Carrera(carreraId, "Ingeniería Informática", 5);
    carrera.setMaterias(new ArrayList<>());

    when(carreraService.buscarPorId(carreraId)).thenReturn(Optional.of(carrera));

    mockMvc.perform(get("/carrera/{carreraId}/materias", carreraId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

    verify(carreraService, times(1)).buscarPorId(carreraId);
}

    @Test
    void obtenerMaterias_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {

    Long carreraIdInexistente = 999L;

    when(carreraService.buscarPorId(carreraIdInexistente)).thenReturn(Optional.empty());

    mockMvc.perform(get("/carrera/{carreraId}/materias", carreraIdInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());

    verify(carreraService, times(1)).buscarPorId(carreraIdInexistente);
}

}