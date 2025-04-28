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
        
        // Configurar MockMvc con el controlador y el manejador de excepciones personalizado
        mockMvc = MockMvcBuilders.standaloneSetup(profesorController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void listarTodos_debeRetornarListaDeProfesores_cuandoHayProfesores() throws Exception {
        // Preparación
        Profesor profesor1 = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        Profesor profesor2 = new Profesor(2L, "María", "González", "Ingeniera en Sistemas");
        
        List<Profesor> profesores = Arrays.asList(profesor1, profesor2);
        
        // Mock del servicio
        when(profesorService.buscarTodos()).thenReturn(profesores);
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarTodos();
    }
    
    @Test
    void listarTodos_debeRetornarListaVacia_cuandoNoHayProfesores() throws Exception {
        // Mock del servicio
        when(profesorService.buscarTodos()).thenReturn(Collections.emptyList());
        
        // Ejecución y verificación
        mockMvc.perform(get("/profesor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarTodos();
    }
    
    @Test
    void buscarPorId_debeRetornarProfesor_cuandoExiste() throws Exception {
        // Preparación
        Long idBuscado = 1L;
        
        Profesor profesor = new Profesor(idBuscado, "Juan", "Pérez", "Licenciado en Informática");
        
        // Agregar algunas materias al profesor
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        profesor.setMaterias(Arrays.asList(materia1, materia2));
        
        // Mock del servicio
        when(profesorService.buscarPorId(idBuscado)).thenReturn(Optional.of(profesor));
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio
        when(profesorService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(get("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarProfesorCreado_cuandoDatosValidos() throws Exception {
        // Preparación
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L));
        
        Profesor profesorCreado = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        // Agregar materias al profesor creado
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        profesorCreado.setMaterias(Arrays.asList(materia1, materia2));
        
        // Mock del servicio
        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorCreado);
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoNombreYApellidoDuplicados() throws Exception {
        // Preparación
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");
        
        // Mock del servicio - simula un error de nombre y apellido duplicados
        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadDuplicadaException("Profesor", "nombre y apellido", "Juan Pérez"));
        
        // Ejecución y verificación
        mockMvc.perform(post("/profesor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void actualizar_debeRetornarProfesorActualizado_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan Actualizado");
        profesorDto.setApellido("Pérez Modificado");
        profesorDto.setTitulo("Doctor en Informática");
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));
        
        Profesor profesorActualizado = new Profesor(idActualizar, "Juan Actualizado", "Pérez Modificado", "Doctor en Informática");
        
        // Agregar materias al profesor actualizado
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
        
        // Mock del servicio
        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorActualizado);
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado y se estableció el ID en el DTO
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setNombre("Juan");
        profesorDto.setApellido("Pérez");
        profesorDto.setTitulo("Licenciado en Informática");
        
        // Mock del servicio - simula un error de profesor no encontrado
        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Profesor", idInexistente));
        
        // Ejecución y verificación
        mockMvc.perform(put("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {
        // Preparación
        Long idEliminar = 1L;
        
        // Mock del servicio - configurar para que no haga nada (éxito)
        doNothing().when(profesorService).eliminarPorId(idEliminar);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/profesor/{id}", idEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).eliminarPorId(idEliminar);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio - simula un error de profesor no encontrado
        doThrow(new EntidadNoEncontradaException("Profesor", idInexistente))
            .when(profesorService).eliminarPorId(idInexistente);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/profesor/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).eliminarPorId(idInexistente);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoProfesorTieneMateriasAsignadas() throws Exception {
        // Preparación
        Long idProfesorConMaterias = 1L;
        
        // Mock del servicio - simula un error de regla de negocio
        doThrow(new ReglaNegocioException("No se puede eliminar el profesor porque tiene materias asignadas"))
            .when(profesorService).eliminarPorId(idProfesorConMaterias);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/profesor/{id}", idProfesorConMaterias)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar el profesor porque tiene materias asignadas"));
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).eliminarPorId(idProfesorConMaterias);
    }
    
    @Test
    void obtenerMateriasOrdenadas_debeRetornarListaDeMaterias_cuandoProfesorExiste() throws Exception {
        // Preparación
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
        
        // Mock del servicio - las materias ya vienen ordenadas alfabéticamente
        when(profesorServiceImpl.obtenerMateriasOrdenadas(profesorId)).thenReturn(materiasOrdenadas);
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado
        verify(profesorServiceImpl, times(1)).obtenerMateriasOrdenadas(profesorId);
    }
    
    @Test
    void obtenerMateriasOrdenadas_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio - simula un error de profesor no encontrado
        when(profesorServiceImpl.obtenerMateriasOrdenadas(idInexistente))
            .thenThrow(new EntidadNoEncontradaException("Profesor", idInexistente));
        
        // Ejecución y verificación
        mockMvc.perform(get("/profesor/{profesorId}/materias", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorServiceImpl, times(1)).obtenerMateriasOrdenadas(idInexistente);
    }
    
    @Test
    void asignarMaterias_debeRetornarProfesorActualizado_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idProfesor = 1L;
        
        Profesor profesorExistente = new Profesor(idProfesor, "Juan", "Pérez", "Licenciado en Informática");
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L, 3L));
        
        // Profesor después de asignar materias
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
        
        // Mock del servicio - verifica que el profesor existe primero
        when(profesorService.buscarPorId(idProfesor)).thenReturn(Optional.of(profesorExistente));
        // Mock para la actualización - verifica que se preservaron los datos originales y se actualizaron las materias
        when(profesorService.guardar(any(ProfesorDto.class))).thenReturn(profesorActualizado);
        
        // Ejecución y verificación
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
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarPorId(idProfesor);
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
    
    @Test
    void asignarMaterias_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 2L));
        
        // Mock del servicio - simula un error de profesor no encontrado
        when(profesorService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(post("/profesor/{id}/materias", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarPorId(idInexistente);
        verify(profesorService, times(0)).guardar(any(ProfesorDto.class)); // No debería llegar a guardar
    }
    
    @Test
    void asignarMaterias_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
        // Preparación
        Long idProfesor = 1L;
        
        Profesor profesorExistente = new Profesor(idProfesor, "Juan", "Pérez", "Licenciado en Informática");
        
        ProfesorDto profesorDto = new ProfesorDto();
        profesorDto.setMateriasIds(Arrays.asList(1L, 999L)); // ID 999 no existe
        
        // Mock del servicio - verifica que el profesor existe primero
        when(profesorService.buscarPorId(idProfesor)).thenReturn(Optional.of(profesorExistente));
        // Mock para la actualización - simula un error de materia no encontrada
        when(profesorService.guardar(any(ProfesorDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Materia", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/profesor/{id}/materias", idProfesor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(profesorService, times(1)).buscarPorId(idProfesor);
        verify(profesorService, times(1)).guardar(any(ProfesorDto.class));
    }
}