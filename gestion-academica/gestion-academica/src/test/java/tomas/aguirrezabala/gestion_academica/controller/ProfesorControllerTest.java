package tomas.aguirrezabala.gestion_academica.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

import tomas.aguirrezabala.gestion_academica.business.ProfesorService;
import tomas.aguirrezabala.gestion_academica.business.impl.ProfesorServiceImpl;
import tomas.aguirrezabala.gestion_academica.controller.handler.CustomResponseEntityExceptionHandler;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.ProfesorDto;

public class ProfesorControllerTest {

    @Mock
    private ProfesorService profesorService;
    
    @Mock
    private ProfesorServiceImpl profesorServiceImpl;
    
    @InjectMocks
    private ProfesorController profesorController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(profesorController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void listarTodos_debeRetornarListaDeProfesores_cuandoHayProfesores() throws Exception {

        Profesor profesor1 = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        Profesor profesor2 = new Profesor(2L, "María", "González", "Ingeniera en Sistemas");
        
        List<Profesor> profesores = Arrays.asList(profesor1, profesor2);

        when(profesorService.buscarTodos()).thenReturn(profesores);

        mockMvc.perform(get("/profesor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Juan")))
                .andExpect(jsonPath("$[0].apellido", is("Pérez")))
                .andExpect(jsonPath("$[0].titulo", is("Licenciado en Informática")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("María")))
                .andExpect(jsonPath("$[1].apellido", is("González")))
                .andExpect(jsonPath("$[1].titulo", is("Ingeniera en Sistemas")));

        verify(profesorService, times(1)).buscarTodos();
    }
    
    @Test
    void listarTodos_debeRetornarListaVacia_cuandoNoHayProfesores() throws Exception {

        when(profesorService.buscarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/profesor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(profesorService, times(1)).buscarTodos();
    }
    
    @Test
    void buscarPorId_debeRetornarProfesor_cuandoExiste() throws Exception {

        Long idBuscado = 1L;
        
        Profesor profesor = new Profesor(idBuscado, "Juan", "Pérez", "Licenciado en Informática");

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        profesor.setMaterias(Arrays.asList(materia1, materia2));

        when(profesorService.buscarPorId(idBuscado)).thenReturn(Optional.of(profesor));

        mockMvc.perform(get("/profesor/{id}", idBuscado)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellido", is("Pérez")))
                .andExpect(jsonPath("$.titulo", is("Licenciado en Informática")))
                .andExpect(jsonPath("$.materias", hasSize(2)))
                .andExpect(jsonPath("$.materias[0].id", is(1)))
                .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
                .andExpect(jsonPath("$.materias[1].id", is(2)))
                .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")));

        verify(profesorService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {

        Long idInexistente = 999L;

        when(profesorService.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(get("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarProfesorCreado_cuandoDatosValidos() throws Exception {

        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L));
        
        Profesor profesorCreado = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        profesorCreado.setMaterias(Arrays.asList(materia1, materia2));

        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorCreado);

        mockMvc.perform(post("/profesor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellido", is("Pérez")))
                .andExpect(jsonPath("$.titulo", is("Licenciado en Informática")))
                .andExpect(jsonPath("$.materias", hasSize(2)))
                .andExpect(jsonPath("$.materias[0].id", is(1)))
                .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
                .andExpect(jsonPath("$.materias[1].id", is(2)))
                .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")));

        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoNombreYApellidoDuplicados() throws Exception {

        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");

        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadDuplicadaException("Profesor", "nombre y apellido", "Juan Pérez"));

        mockMvc.perform(post("/profesor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void actualizar_debeRetornarProfesorActualizado_cuandoDatosValidos() throws Exception {

        Long idActualizar = 1L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan Actualizado");
        profesorDto.setApellido("Pérez Modificado");
        profesorDto.setTitulo("Doctor en Informática");
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));
        
        Profesor profesorActualizado = new Profesor(idActualizar, "Juan Actualizado", "Pérez Modificado", "Doctor en Informática");

        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        Materia materia3 = new Materia();
        materia3.setId(3L);
        materia3.setNombre("Sistemas Operativos");
        
        profesorActualizado.setMaterias(Arrays.asList(materia1, materia2, materia3));

        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorActualizado);

        mockMvc.perform(put("/profesor/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Actualizado")))
                .andExpect(jsonPath("$.apellido", is("Pérez Modificado")))
                .andExpect(jsonPath("$.titulo", is("Doctor en Informática")))
                .andExpect(jsonPath("$.materias", hasSize(3)))
                .andExpect(jsonPath("$.materias[2].id", is(3)))
                .andExpect(jsonPath("$.materias[2].nombre", is("Sistemas Operativos")));

        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {

        Long idInexistente = 999L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");

        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Profesor", idInexistente));

        mockMvc.perform(put("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {

        Long idEliminar = 1L;

        doNothing().when(profesorService).eliminarPorId(idEliminar);

        mockMvc.perform(delete("/profesor/{id}", idEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(profesorService, times(1)).eliminarPorId(idEliminar);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {

        Long idInexistente = 999L;

        doThrow(new EntidadNoEncontradaException("Profesor", idInexistente))
            .when(profesorService).eliminarPorId(idInexistente);

        mockMvc.perform(delete("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).eliminarPorId(idInexistente);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoProfesorTieneMateriasAsignadas() throws Exception {

        Long idProfesorConMaterias = 1L;

        doThrow(new ReglaNegocioException("No se puede eliminar el profesor porque tiene materias asignadas"))
            .when(profesorService).eliminarPorId(idProfesorConMaterias);

        mockMvc.perform(delete("/profesor/{id}", idProfesorConMaterias)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar el profesor porque tiene materias asignadas"));

        verify(profesorService, times(1)).eliminarPorId(idProfesorConMaterias);
    }
    
    @Test
    void obtenerMateriasOrdenadas_debeRetornarListaDeMaterias_cuandoProfesorExiste() throws Exception {

        Long profesorId = 1L;
        
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Análisis Matemático");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        Materia materia3 = new Materia();
        materia3.setId(3L);
        materia3.setNombre("Programación I");
        
        List<Materia> materiasOrdenadas = Arrays.asList(materia1, materia2, materia3);

        when(profesorServiceImpl.obtenerMateriasOrdenadas(profesorId)).thenReturn(materiasOrdenadas);

        mockMvc.perform(get("/profesor/{profesorId}/materias", profesorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Análisis Matemático")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Base de Datos")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].nombre", is("Programación I")));

        verify(profesorServiceImpl, times(1)).obtenerMateriasOrdenadas(profesorId);
    }
    
    @Test
    void obtenerMateriasOrdenadas_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {

        Long idInexistente = 999L;

        when(profesorServiceImpl.obtenerMateriasOrdenadas(idInexistente))
            .thenThrow(new EntidadNoEncontradaException("Profesor", idInexistente));

        mockMvc.perform(get("/profesor/{profesorId}/materias", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorServiceImpl, times(1)).obtenerMateriasOrdenadas(idInexistente);
    }
    
    @Test
    void asignarMaterias_debeRetornarProfesorActualizado_cuandoDatosValidos() throws Exception {

        Long idProfesor = 1L;
        
        Profesor profesorExistente = new Profesor(idProfesor, "Juan", "Pérez", "Licenciado en Informática");
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));

        Profesor profesorActualizado = new Profesor(idProfesor, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        Materia materia3 = new Materia();
        materia3.setId(3L);
        materia3.setNombre("Sistemas Operativos");
        
        profesorActualizado.setMaterias(Arrays.asList(materia1, materia2, materia3));

        when(profesorService.buscarPorId(idProfesor)).thenReturn(Optional.of(profesorExistente));

        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorActualizado);

        mockMvc.perform(post("/profesor/{id}/materias", idProfesor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.apellido", is("Pérez")))
                .andExpect(jsonPath("$.titulo", is("Licenciado en Informática")))
                .andExpect(jsonPath("$.materias", hasSize(3)))
                .andExpect(jsonPath("$.materias[0].id", is(1)))
                .andExpect(jsonPath("$.materias[0].nombre", is("Programación I")))
                .andExpect(jsonPath("$.materias[1].id", is(2)))
                .andExpect(jsonPath("$.materias[1].nombre", is("Base de Datos")))
                .andExpect(jsonPath("$.materias[2].id", is(3)))
                .andExpect(jsonPath("$.materias[2].nombre", is("Sistemas Operativos")));

        verify(profesorService, times(1)).buscarPorId(idProfesor);
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void asignarMaterias_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {

        Long idInexistente = 999L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L));

        when(profesorService.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(post("/profesor/{id}/materias", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).buscarPorId(idInexistente);
        verify(profesorService, times(0)).guardar(any(ProfesorDto.class)); // No debería llegar a guardar
    }
    
    @Test
    void asignarMaterias_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
 
        Long idProfesor = 1L;
        
        Profesor profesorExistente = new Profesor(idProfesor, "Juan", "Pérez", "Licenciado en Informática");
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 999L)); 

        when(profesorService.buscarPorId(idProfesor)).thenReturn(Optional.of(profesorExistente));

        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Materia", 999L));

        mockMvc.perform(post("/profesor/{id}/materias", idProfesor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(profesorService, times(1)).buscarPorId(idProfesor);
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
}