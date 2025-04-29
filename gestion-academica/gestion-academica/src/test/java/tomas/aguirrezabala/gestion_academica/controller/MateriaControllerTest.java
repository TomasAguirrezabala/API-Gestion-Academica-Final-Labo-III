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

        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materia1 = new Materia(1L, "Programación I", 1, 1, profesor);
        Materia materia2 = new Materia(2L, "Base de Datos", 2, 1);
        
        List<Materia> materias = Arrays.asList(materia1, materia2);

        when(materiaService.buscarTodas()).thenReturn(materias);

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

        verify(materiaService, times(1)).buscarTodas();
    }
    
    @Test
    void listarTodas_debeRetornarListaVacia_cuandoNoHayMaterias() throws Exception {

        when(materiaService.buscarTodas()).thenReturn(Collections.emptyList());
        

        mockMvc.perform(get("/materia")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(materiaService, times(1)).buscarTodas();
    }
    
    @Test
    void buscarPorId_debeRetornarMateria_cuandoExiste() throws Exception {

        Long idBuscado = 1L;
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materia = new Materia(idBuscado, "Programación I", 1, 1, profesor);
        materia.setCorrelatividades(Arrays.asList(2L, 3L));

        when(materiaService.buscarPorId(idBuscado)).thenReturn(Optional.of(materia));

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

        verify(materiaService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {

        Long idInexistente = 999L;

        when(materiaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(get("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarMateriaCreada_cuandoDatosValidos() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(1L);
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materiaCreada = new Materia(1L, "Programación I", 1, 1, profesor);

        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);

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

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoNombreDuplicado() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);

        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadDuplicadaException("Materia", "nombre", "Programación I"));

        mockMvc.perform(post("/materia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoProfesorNoExiste() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(999L); 

        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Profesor", 999L));

        mockMvc.perform(post("/materia")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void actualizar_debeRetornarMateriaActualizada_cuandoDatosValidos() throws Exception {

        Long idActualizar = 1L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I - Actualizada");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(2);
        materiaDto.setProfesorId(2L);
        
        Profesor profesor = new Profesor(2L, "María", "González", "Ingeniera en Sistemas");
        
        Materia materiaActualizada = new Materia(idActualizar, "Programación I - Actualizada", 2, 2, profesor);

        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaActualizada);

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

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

        Long idInexistente = 999L;
        
        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);

        when(materiaService.guardar(any(MateriaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Materia", idInexistente));

        mockMvc.perform(put("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
    }
    
    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {

        Long idEliminar = 1L;

        doNothing().when(materiaService).eliminarPorId(idEliminar);

        mockMvc.perform(delete("/materia/{id}", idEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(materiaService, times(1)).eliminarPorId(idEliminar);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

        Long idInexistente = 999L;

        doThrow(new EntidadNoEncontradaException("Materia", idInexistente))
            .when(materiaService).eliminarPorId(idInexistente);

        mockMvc.perform(delete("/materia/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).eliminarPorId(idInexistente);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaEsCorrelativa() throws Exception {

        Long idMateria = 1L;

        doThrow(new ReglaNegocioException("No se puede eliminar la materia porque es correlativa de: Programación II"))
            .when(materiaService).eliminarPorId(idMateria);

        mockMvc.perform(delete("/materia/{id}", idMateria)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la materia porque es correlativa de: Programación II"));
 
        verify(materiaService, times(1)).eliminarPorId(idMateria);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoMateriaConInscripciones() throws Exception {

        Long idMateria = 1L;

        doThrow(new ReglaNegocioException("No se puede eliminar la materia porque tiene alumnos inscriptos"))
            .when(materiaService).eliminarPorId(idMateria);

        mockMvc.perform(delete("/materia/{id}", idMateria)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar la materia porque tiene alumnos inscriptos"));

        verify(materiaService, times(1)).eliminarPorId(idMateria);
    }
    
    @Test
    void crearConCorrelatividades_debeRetornarMateriaCreada_cuandoDatosValidos() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación II");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(1);
        materiaDto.setProfesorId(1L);
        materiaDto.setCorrelatividades(Arrays.asList(1L, 2L)); 
        
        Profesor profesor = new Profesor(1L, "Juan", "Pérez", "Licenciado en Informática");
        
        Materia materiaCreada = new Materia(3L, "Programación II", 2, 1, profesor);
        materiaCreada.setCorrelatividades(Arrays.asList(1L, 2L));

        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList())).thenReturn(materiaCreada);

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

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void crearConCorrelatividades_debeLanzarExcepcion_cuandoCorrelativaNoExiste() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación II");
        materiaDto.setAnio(2);
        materiaDto.setCuatrimestre(1);
        materiaDto.setCorrelatividades(Arrays.asList(1L, 999L)); 
        
        Materia materiaCreada = new Materia(3L, "Programación II", 2, 1);

        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new EntidadNoEncontradaException("Materia correlativa", 999L));

        mockMvc.perform(post("/materia/con-correlatividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void crearConCorrelatividades_debeLanzarExcepcion_cuandoCreaCiclo() throws Exception {

        MateriaDto materiaDto = new MateriaDto();
        materiaDto.setNombre("Programación I");
        materiaDto.setAnio(1);
        materiaDto.setCuatrimestre(1);
        materiaDto.setCorrelatividades(Arrays.asList(2L, 3L)); 
        
        Materia materiaCreada = new Materia(1L, "Programación I", 1, 1);

        when(materiaService.guardar(any(MateriaDto.class))).thenReturn(materiaCreada);
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new ReglaNegocioException("La correlatividad crearía un ciclo, lo cual no está permitido"));

        mockMvc.perform(post("/materia/con-correlatividades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(materiaDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje").value("La correlatividad crearía un ciclo, lo cual no está permitido"));

        verify(materiaService, times(1)).guardar(any(MateriaDto.class));
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeRetornarMateriaActualizada_cuandoDatosValidos() throws Exception {

        Long idMateria = 1L;
        List<Long> correlatividades = Arrays.asList(2L, 3L);
        
        Materia materia = new Materia(idMateria, "Programación I", 1, 1);
        Materia materiaActualizada = new Materia(idMateria, "Programación I", 1, 1);
        materiaActualizada.setCorrelatividades(correlatividades);

        when(materiaService.buscarPorId(idMateria)).thenReturn(Optional.of(materia));
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList())).thenReturn(materiaActualizada);

        mockMvc.perform(post("/materia/{id}/correlatividades", idMateria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Programación I")))
                .andExpect(jsonPath("$.correlatividades", hasSize(2)))
                .andExpect(jsonPath("$.correlatividades[0]", is(2)))
                .andExpect(jsonPath("$.correlatividades[1]", is(3)));

        verify(materiaService, times(1)).buscarPorId(idMateria);
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {

        Long idInexistente = 999L;
        List<Long> correlatividades = Arrays.asList(1L, 2L);

        when(materiaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(post("/materia/{id}/correlatividades", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).buscarPorId(idInexistente);
        verify(materiaService, times(0)).crearConCorrelatividades(any(Materia.class), anyList());
    }
    
    @Test
    void agregarCorrelatividades_debeLanzarExcepcion_cuandoCorrelativaNoExiste() throws Exception {

        Long idMateria = 1L;
        List<Long> correlatividades = Arrays.asList(2L, 999L); // 999L no existe
        
        Materia materia = new Materia(idMateria, "Programación I", 1, 1);

        when(materiaService.buscarPorId(idMateria)).thenReturn(Optional.of(materia));
        when(materiaService.crearConCorrelatividades(any(Materia.class), anyList()))
            .thenThrow(new EntidadNoEncontradaException("Materia correlativa", 999L));

        mockMvc.perform(post("/materia/{id}/correlatividades", idMateria)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(correlatividades)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());

        verify(materiaService, times(1)).buscarPorId(idMateria);
        verify(materiaService, times(1)).crearConCorrelatividades(any(Materia.class), anyList());
    }
}