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

import tomas.aguirrezabala.gestion_academica.business.AsignaturaService;
import tomas.aguirrezabala.gestion_academica.controller.handler.CustomResponseEntityExceptionHandler;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AsignaturaDto;

public class AsignaturaControllerTest {
    
    @Mock
    private AsignaturaService asignaturaService;
    
    @InjectMocks
    private AsignaturaController asignaturaController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar MockMvc con el controlador y el manejador de excepciones personalizado
        mockMvc = MockMvcBuilders.standaloneSetup(asignaturaController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void listarTodas_debeRetornarListaDeAsignaturas_cuandoHayAsignaturas() throws Exception {
        // Preparación
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        Asignatura asignatura1 = new Asignatura(1L, materia1, alumno, EstadoAsignatura.CURSANDO);
        Asignatura asignatura2 = new Asignatura(2L, materia2, alumno, EstadoAsignatura.APROBADO, 8.5);
        
        List<Asignatura> asignaturas = Arrays.asList(asignatura1, asignatura2);
        
        // Mock del servicio
        when(asignaturaService.buscarTodas()).thenReturn(asignaturas);
        
        // Ejecución y verificación
        mockMvc.perform(get("/asignatura")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].estado", is("CURSANDO")))
                .andExpect(jsonPath("$[0].materia.nombre", is("Programación I")))
                .andExpect(jsonPath("$[0].alumno.nombre", is("Tomas")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].estado", is("APROBADO")))
                .andExpect(jsonPath("$[1].nota", is(8.5)))
                .andExpect(jsonPath("$[1].materia.nombre", is("Base de Datos")));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarTodas();
    }
    
    @Test
    void listarTodas_debeRetornarListaVacia_cuandoNoHayAsignaturas() throws Exception {
        // Mock del servicio
        when(asignaturaService.buscarTodas()).thenReturn(Collections.emptyList());
        
        // Ejecución y verificación
        mockMvc.perform(get("/asignatura")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarTodas();
    }
    
    @Test
    void buscarPorId_debeRetornarAsignatura_cuandoExiste() throws Exception {
        // Preparación
        Long idBuscado = 1L;
        
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        
        Asignatura asignatura = new Asignatura(idBuscado, materia, alumno, EstadoAsignatura.CURSANDO);
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idBuscado)).thenReturn(Optional.of(asignatura));
        
        // Ejecución y verificación
        mockMvc.perform(get("/asignatura/{id}", idBuscado)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("CURSANDO")))
                .andExpect(jsonPath("$.materia.id", is(1)))
                .andExpect(jsonPath("$.alumno.id", is(1)));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(get("/asignatura/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarAsignaturaCreada_cuandoDatosValidos() throws Exception {
        // Preparación
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(1L);
        asignaturaDto.setMateriaId(1L);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaCreada = new Asignatura(1L, materia, alumno, EstadoAsignatura.CURSANDO);
        
        // Mock del servicio
        when(asignaturaService.guardar(any(AsignaturaDto.class))).thenReturn(asignaturaCreada);
        
        // Ejecución y verificación
        mockMvc.perform(post("/asignatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("CURSANDO")))
                .andExpect(jsonPath("$.materia.nombre", is("Programación I")))
                .andExpect(jsonPath("$.alumno.nombre", is("Tomas")));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
        // Preparación
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(999L); // ID de alumno inexistente
        asignaturaDto.setMateriaId(1L);
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        // Mock del servicio - simula un error de alumno no encontrado
        when(asignaturaService.guardar(any(AsignaturaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Alumno", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/asignatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
        // Preparación
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(1L);
        asignaturaDto.setMateriaId(999L); // ID de materia inexistente
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        // Mock del servicio - simula un error de materia no encontrada
        when(asignaturaService.guardar(any(AsignaturaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Materia", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/asignatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoAsignaturaYaExiste() throws Exception {
        // Preparación
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(1L);
        asignaturaDto.setMateriaId(1L); // Combinación alumno-materia ya existe
        asignaturaDto.setEstado(EstadoAsignatura.CURSANDO);
        
        // Mock del servicio - simula un error de asignatura duplicada
        when(asignaturaService.guardar(any(AsignaturaDto.class)))
            .thenThrow(new EntidadDuplicadaException("Asignatura", "alumno y materia", "Alumno ID: 1, Materia ID: 1"));
        
        // Ejecución y verificación
        mockMvc.perform(post("/asignatura")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void actualizar_debeRetornarAsignaturaActualizada_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(1L);
        asignaturaDto.setMateriaId(1L);
        asignaturaDto.setEstado(EstadoAsignatura.REGULAR);
        
        Asignatura asignaturaActualizada = new Asignatura(idActualizar, materia, alumno, EstadoAsignatura.REGULAR);
        
        // Mock del servicio
        when(asignaturaService.guardar(any(AsignaturaDto.class))).thenReturn(asignaturaActualizada);
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("REGULAR")))
                .andExpect(jsonPath("$.materia.nombre", is("Programación I")))
                .andExpect(jsonPath("$.alumno.nombre", is("Tomas")));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoAsignaturaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setAlumnoId(1L);
        asignaturaDto.setMateriaId(1L);
        asignaturaDto.setEstado(EstadoAsignatura.REGULAR);
        
        // Mock del servicio - simula un error de asignatura no encontrada
        when(asignaturaService.guardar(any(AsignaturaDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Asignatura", idInexistente));
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {
        // Preparación
        Long idEliminar = 1L;
        
        // Mock del servicio - configurar para que no haga nada (éxito)
        doNothing().when(asignaturaService).eliminarPorId(idEliminar);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/asignatura/{id}", idEliminar)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).eliminarPorId(idEliminar);
    }
    
    @Test
    void eliminar_debeLanzarExcepcion_cuandoAsignaturaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio - simula un error de asignatura no encontrada
        doThrow(new EntidadNoEncontradaException("Asignatura", idInexistente))
            .when(asignaturaService).eliminarPorId(idInexistente);
        
        // Ejecución y verificación
        mockMvc.perform(delete("/asignatura/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).eliminarPorId(idInexistente);
    }
    
    @Test
    void actualizarEstado_debeRetornarAsignaturaActualizada_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idAsignatura = 1L;
        EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
        
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        
        Asignatura asignatura = new Asignatura(idAsignatura, materia, alumno, EstadoAsignatura.CURSANDO);
        
        Asignatura asignaturaActualizada = new Asignatura(idAsignatura, materia, alumno, EstadoAsignatura.APROBADO, 7.0);
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idAsignatura)).thenReturn(Optional.of(asignatura));
        when(asignaturaService.guardar(any(AsignaturaDto.class))).thenReturn(asignaturaActualizada);
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}/estado", idAsignatura)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoEstado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("APROBADO")))
                .andExpect(jsonPath("$.nota", is(7.0)));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idAsignatura);
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void actualizarEstado_debeLanzarExcepcion_cuandoAsignaturaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}/estado", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoEstado)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idInexistente);
        verify(asignaturaService, times(0)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void actualizarNota_debeRetornarAsignaturaActualizada_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idAsignatura = 1L;
        Double nuevaNota = 8.5;
        
        Carrera carrera = new Carrera(1L, "Ingeniería Informática", 5);
        
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Tomas");
        alumno.setApellido("Aguirrezabala");
        alumno.setCarrera(carrera);
        
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        
        Asignatura asignatura = new Asignatura(idAsignatura, materia, alumno, EstadoAsignatura.REGULAR);
        
        // La nota es >= 7, así que el estado debería cambiar automáticamente a APROBADO
        Asignatura asignaturaActualizada = new Asignatura(idAsignatura, materia, alumno, EstadoAsignatura.APROBADO, nuevaNota);
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idAsignatura)).thenReturn(Optional.of(asignatura));
        when(asignaturaService.guardar(any(AsignaturaDto.class))).thenReturn(asignaturaActualizada);
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}/nota", idAsignatura)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaNota)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("APROBADO")))
                .andExpect(jsonPath("$.nota", is(8.5)));
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idAsignatura);
        verify(asignaturaService, times(1)).guardar(any(AsignaturaDto.class));
    }
    
    @Test
    void actualizarNota_debeLanzarExcepcion_cuandoAsignaturaNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        Double nuevaNota = 8.5;
        
        // Mock del servicio
        when(asignaturaService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(put("/asignatura/{id}/nota", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaNota)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(asignaturaService, times(1)).buscarPorId(idInexistente);
        verify(asignaturaService, times(0)).guardar(any(AsignaturaDto.class));
    }
}