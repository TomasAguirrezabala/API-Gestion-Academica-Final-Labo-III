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
import static org.mockito.ArgumentMatchers.anyList;
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

import tomas.aguirrezabala.gestion_academica.business.MateriaService;
import tomas.aguirrezabala.gestion_academica.controller.handler.CustomResponseEntityExceptionHandler;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.MateriaDto;

public class MateriaControllerTest {

    @Mock
    private MateriaService materiaService;
    
    @InjectMocks
    private MateriaController materiaController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar MockMvc con el controlador y el manejador de excepciones personalizado
        mockMvc = MockMvcBuilders.standaloneSetup(materiaController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void listarTodas_debeRetornarListaDeMaterias_cuandoHayMaterias() throws Exception {
        // Preparación
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materia1 = new Materia(1L, "Programación I", 1, 1, profesor);
        Materia materia2 = new Materia(2L, "Base de Datos", 2, 1);
        
        List<Materia> materias = Arrays.asList(materia1, materia2);
        
        // Mock del servicio
        when(materiaService.buscarTodas()).thenReturn(materias);
        
        // Ejecución y verificación
        mockMvc.perform(get("/materia")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Programación I")))
                .andExpect(jsonPath("$[0].anio", is(1)))
                .andExpect(jsonPath("$[0].cuatrimestre", is(1)))
                .andExpect(jsonPath("$[0].profesor.nombre", is("Juan")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Base de Datos")))
                .andExpect(jsonPath("$[1].anio", is(2)))
                .andExpect(jsonPath("$[1].cuatrimestre", is(1)))
                .andExpect(jsonPath("$[1].profesor").doesNotExist());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).buscarTodas();
    }
    
    @Test
    void listarTodas_debeRetornarListaVacia_cuandoNoHayMaterias() throws Exception {
        // Mock del servicio
        when(materiaService.buscarTodas()).thenReturn(Collections.emptyList());
        
        // Ejecución y verificación
        mockMvc.perform(get("/materia")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).buscarTodas();
    }
    
    @Test
    void buscarPorId_debeRetornarMateria_cuandoExiste() throws Exception {
        // Preparación
        Long idBuscado = 1L;
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materia = new Materia(idBuscado, "Programación I", 1, 1, profesor);
        materia.setCorrelatividades(Arrays.asList(2L, 3L));
        
        // Mock del servicio
        when(materiaService.buscarPorId(idBuscado)).thenReturn(Optional.of(materia));
        
        // Ejecución y verificación
        mockMvc.perform(get("/materia/{id}", idBuscado)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación I")))
                .andExpect(jsonPath("$.anio", is(1)))
                .andExpect(jsonPath("$.cuatrimestre", is(1)))
                .andExpect(jsonPath("$.profesor.id", is(1)))
                .andExpect(jsonPath("$.profesor.nombre", is("Juan")))
                .andExpect(jsonPath("$.correlatividades", hasSize(2)))
                .andExpect(jsonPath("$.correlatividades[0]", is(2)))
                .andExpect(jsonPath("$.correlatividades[1]", is(3)));
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio
        when(materiaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(get("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarMateriaCreada_cuandoDatosValidos() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(1L);
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materiaCreada = new Materia(1L, "Programación I", 1, 1, profesor);
        
        // Mock del servicio
        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación I")))
                .andExpect(jsonPath("$.anio", is(1)))
                .andExpect(jsonPath("$.cuatrimestre", is(1)))
                .andExpect(jsonPath("$.profesor.id", is(1)))
                .andExpect(jsonPath("$.profesor.nombre", is("Juan")));
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        
        // Mock del servicio - simula un error de nombre duplicado
        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadDuplicadaException("Materia", "nombre", "Programación I"));
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(999L); // ID inexistente
        
        // Mock del servicio - simula un error de profesor no encontrado
        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Profesor", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void actualizar_debeRetornarMateriaActualizada_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I - Actualizada");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(2);
        materiaDto.setProfesorId(2L);
        
        Profesor profesor = new Profesor(2L, "María", "González", "Ingeniera en Sistemas");
        
        Materia materiaActualizada = new Materia(idActualizar, "Programación I - Actualizada", 2, 2, profesor);
        
        // Mock del servicio
        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaActualizada);
        
        // Ejecución y verificación
        mockMvc.perform(put("/materia/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación I - Actualizada")))
                .andExpect(jsonPath("$.anio", is(2)))
                .andExpect(jsonPath("$.cuatrimestre", is(2)))
                .andExpect(jsonPath("$.profesor.id", is(2)))
                .andExpect(jsonPath("$.profesor.nombre", is("María")));
        
        // Verificar que el servicio fue llamado y se estableció el ID en el DTO
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        
        // Mock del servicio - simula un error de materia no encontrada
        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Materia", idInexistente));
        
        // Ejecución y verificación
        mockMvc.perform(put("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {
        // Preparación
        Long idEliminar = 1L;
        
        // Mock del servicio - configurar para que no haga nada (éxito)
        doNothing().when(materiaService).eliminarPorId(idEliminar);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/materia/{id}", idEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).eliminarPorId(idEliminar);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio - simula un error de materia no encontrada
        doThrow(new EntidadNoEncontradaException("Materia", idInexistente))
            .when(materiaService).eliminarPorId(idInexistente);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).eliminarPorId(idInexistente);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaEsCorrelativa() throws Exception {
        // Preparación
        Long idMateria = 1L;
        
        // Mock del servicio - simula un error de materia usada como correlativa
        doThrow(new ReglaNegocioException("No se puede eliminar la materia porque es correlativa de: Programación II"))
            .when(materiaService).eliminarPorId(idMateria);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/materia/{id}", idMateria)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la materia porque es correlativa de: Programación II"));
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).eliminarPorId(idMateria);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaConInscripciones() throws Exception {
        // Preparación
        Long idMateria = 1L;
        
        // Mock del servicio - simula un error de materia con alumnos inscriptos
        doThrow(new ReglaNegocioException("No se puede eliminar la materia porque tiene alumnos inscriptos"))
            .when(materiaService).eliminarPorId(idMateria);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/materia/{id}", idMateria)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la materia porque tiene alumnos inscriptos"));
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).eliminarPorId(idMateria);
    }
    
    @Test
    void crearConCorrelatividades_debeRetornarMateriaCreada_cuandoDatosValidos() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación II");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(1L);
        materiaDto.setCorrelatividades(Arrays.asList(1L, 2L)); // IDs de materias correlativas
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materiaCreada = new Materia(3L, "Programación II", 2, 1, profesor);
        materiaCreada.setCorrelatividades(Arrays.asList(1L, 2L));
        
        // Mock del servicio
        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList())).thenReturn(materiaCreada);
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/con-correlatividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Programación II")))
                .andExpect(jsonPath("$.anio", is(2)))
                .andExpect(jsonPath("$.cuatrimestre", is(1)))
                .andExpect(jsonPath("$.profesor.id", is(1)))
                .andExpect(jsonPath("$.correlatividades", hasSize(2)))
                .andExpect(jsonPath("$.correlatividades[0]", is(1)))
                .andExpect(jsonPath("$.correlatividades[1]", is(2)));
        
        // Verificar que los servicios fueron llamados
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void crearConCorrelatividades_debeLanzarExcepcion_cuandoCorrelativaNoExiste() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación II");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(1);
        materiaDto.setCorrelatividades(Arrays.asList(1L, 999L)); // 999L no existe
        
        Materia materiaCreada = new Materia(3L, "Programación II", 2, 1);
        
        // Mock del servicio
        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new EntidadNoEncontradaException("Materia correlativa", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/con-correlatividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que los servicios fueron llamados
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void crearConCorrelatividades_debeLanzarExcepcion_cuandoCreaCiclo() throws Exception {
        // Preparación
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setCorrelatividades(Arrays.asList(2L, 3L)); // Crearía un ciclo
        
        Materia materiaCreada = new Materia(1L, "Programación I", 1, 1);
        
        // Mock del servicio
        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new ReglaNegocioException("La correlatividad crearía un ciclo, lo cual no está permitido"));
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/con-correlatividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("La correlatividad crearía un ciclo, lo cual no está permitido"));
        
        // Verificar que los servicios fueron llamados
        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeRetornarMateriaActualizada_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idMateria = 1L;
        List<Long> correlatividades = Arrays.asList(2L, 3L);
        
        Materia materia = new Materia(idMateria, "Programación I", 1, 1);
        Materia materiaActualizada = new Materia(idMateria, "Programación I", 1, 1);
        materiaActualizada.setCorrelatividades(correlatividades);
        
        // Mock del servicio
        when(materiaService.buscarPorId(idMateria)).thenReturn(Optional.of(materia));
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList())).thenReturn(materiaActualizada);
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/{id}/correlatividades", idMateria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación I")))
                .andExpect(jsonPath("$.correlatividades", hasSize(2)))
                .andExpect(jsonPath("$.correlatividades[0]", is(2)))
                .andExpect(jsonPath("$.correlatividades[1]", is(3)));
        
        // Verificar que los servicios fueron llamados
        verify(materiaService, times(1)).buscarPorId(idMateria);
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        List<Long> correlatividades = Arrays.asList(1L, 2L);
        
        // Mock del servicio
        when(materiaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/{id}/correlatividades", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(materiaService, times(1)).buscarPorId(idInexistente);
        verify(materiaService, times(0)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeLanzarExcepcion_cuandoCorrelativaNoExiste() throws Exception {
        // Preparación
        Long idMateria = 1L;
        List<Long> correlatividades = Arrays.asList(2L, 999L); // 999L no existe
        
        Materia materia = new Materia(idMateria, "Programación I", 1, 1);
        
        // Mock del servicio
        when(materiaService.buscarPorId(idMateria)).thenReturn(Optional.of(materia));
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new EntidadNoEncontradaException("Materia correlativa", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/materia/{id}/correlatividades", idMateria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que los servicios fueron llamados
        verify(materiaService, times(1)).buscarPorId(idMateria);
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
}