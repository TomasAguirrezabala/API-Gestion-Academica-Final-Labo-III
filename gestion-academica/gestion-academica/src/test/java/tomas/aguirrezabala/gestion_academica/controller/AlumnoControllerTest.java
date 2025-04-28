package tomas.aguirrezabala.gestion_academica.controller;

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

import tomas.aguirrezabala.gestion_academica.business.AlumnoService;
import tomas.aguirrezabala.gestion_academica.controller.handler.CustomResponseEntityExceptionHandler;
import tomas.aguirrezabala.gestion_academica.exception.EntidadDuplicadaException;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.exception.ReglaNegocioException;
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.AlumnoDto;

public class AlumnoControllerTest {

    @Mock
    private AlumnoService alumnoService;

    @InjectMocks
    private AlumnoController alumnoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configurar MockMvc con el controlador y el manejador de excepciones personalizado
        mockMvc = MockMvcBuilders.standaloneSetup(alumnoController)
                .setControllerAdvice(new CustomResponseEntityExceptionHandler())
                .build();
        
        objectMapper = new ObjectMapper();
    }

    @Test
    void listarTodos_debeRetornarListaDeAlumnos_cuandoHayAlumnos() throws Exception {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería Informática");

        Alumno alumno1 = new Alumno(1L, "Tomas", "Aguirrezabala", "12345678", carrera);
        Alumno alumno2 = new Alumno(2L, "Juan", "Perez", "87654321", carrera);
        
        List<Alumno> alumnos = Arrays.asList(alumno1, alumno2);
        
        // Mock del servicio
        when(alumnoService.buscarTodos()).thenReturn(alumnos);
        
        // Ejecución y verificación
        mockMvc.perform(get("/alumno")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Tomas")))
                .andExpect(jsonPath("$[0].apellido", is("Aguirrezabala")))
                .andExpect(jsonPath("$[0].dni", is("12345678")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Juan")))
                .andExpect(jsonPath("$[1].apellido", is("Perez")))
                .andExpect(jsonPath("$[1].dni", is("87654321")));
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).buscarTodos();
    }
    
    @Test
    void listarTodos_debeRetornarListaVacia_cuandoNoHayAlumnos() throws Exception {
        // Mock del servicio
        when(alumnoService.buscarTodos()).thenReturn(Collections.emptyList());
        
        // Ejecución y verificación
        mockMvc.perform(get("/alumno")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).buscarTodos();
    }
    
    @Test
    void buscarPorId_debeRetornarAlumno_cuandoExiste() throws Exception {
        // Preparación
        Long idBuscado = 1L;
        
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería Informática");
        
        Alumno alumno = new Alumno(idBuscado, "Tomas", "Aguirrezabala", "12345678", carrera);
        
        // Mock del servicio
        when(alumnoService.buscarPorId(idBuscado)).thenReturn(Optional.of(alumno));
        
        // Ejecución y verificación
        mockMvc.perform(get("/alumno/{id}", idBuscado)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Tomas")))
                .andExpect(jsonPath("$.apellido", is("Aguirrezabala")))
                .andExpect(jsonPath("$.dni", is("12345678")));
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).buscarPorId(idBuscado);
    }
    
    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        // Mock del servicio
        when(alumnoService.buscarPorId(idInexistente)).thenReturn(Optional.empty());
        
        // Ejecución y verificación
        mockMvc.perform(get("/alumno/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).buscarPorId(idInexistente);
    }
    
    @Test
    void crear_debeRetornarAlumnoCreado_cuandoDatosValidos() throws Exception {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería Informática");
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("12345678");
        alumnoDto.setCarreraId(1L);
        
        Alumno alumnoCreado = new Alumno(1L, "Tomas", "Aguirrezabala", "12345678", carrera);
        
        // Mock del servicio
        when(alumnoService.guardar(any(AlumnoDto.class))).thenReturn(alumnoCreado);
        
        // Ejecución y verificación
        mockMvc.perform(post("/alumno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Tomas")))
                .andExpect(jsonPath("$.apellido", is("Aguirrezabala")))
                .andExpect(jsonPath("$.dni", is("12345678")));
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoDniDuplicado() throws Exception {
        // Preparación
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("12345678"); // DNI duplicado
        alumnoDto.setCarreraId(1L);
        
        // Mock del servicio - simula un error de DNI duplicado
        when(alumnoService.guardar(any(AlumnoDto.class)))
            .thenThrow(new EntidadDuplicadaException("Alumno", "DNI", "12345678"));
        
        // Ejecución y verificación
        mockMvc.perform(post("/alumno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }
    
    @Test
    void crear_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
        // Preparación
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("12345678");
        alumnoDto.setCarreraId(999L); // ID de carrera inexistente
        
        // Mock del servicio - simula un error de carrera no encontrada
        when(alumnoService.guardar(any(AlumnoDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Carrera", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(post("/alumno")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }

    @Test
    void actualizar_debeRetornarAlumnoActualizado_cuandoDatosValidos() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        Carrera carrera = new Carrera();
        carrera.setId(1L);
        carrera.setNombre("Ingeniería Informática");
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas Actualizado");
        alumnoDto.setApellido("Aguirrezabala Modificado");
        alumnoDto.setDni("12345678");
        alumnoDto.setCarreraId(1L);
        
        Alumno alumnoActualizado = new Alumno(idActualizar, "Tomas Actualizado", "Aguirrezabala Modificado", "12345678", carrera);
        
        // Mock del servicio - verificamos que se llama con el ID establecido en el DTO
        when(alumnoService.guardar(any(AlumnoDto.class))).thenAnswer(invocation -> {
            AlumnoDto dto = invocation.getArgument(0);
            assertEquals(idActualizar, dto.getId(), "El ID en el DTO debe ser establecido al valor del path variable");
            return alumnoActualizado;
        });
        
        // Ejecución y verificación
        mockMvc.perform(put("/alumno/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Tomas Actualizado")))
                .andExpect(jsonPath("$.apellido", is("Aguirrezabala Modificado")))
                .andExpect(jsonPath("$.dni", is("12345678")));
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
        // Preparación
        Long idInexistente = 999L;
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("12345678");
        alumnoDto.setCarreraId(1L);
        
        // Mock del servicio - simula un error de alumno no encontrado
        when(alumnoService.guardar(any(AlumnoDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Alumno", idInexistente));
        
        // Ejecución y verificación
        mockMvc.perform(put("/alumno/{id}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoDniDuplicado() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("87654321"); // DNI duplicado con otro alumno
        alumnoDto.setCarreraId(1L);
        
        // Mock del servicio - simula un error de DNI duplicado
        when(alumnoService.guardar(any(AlumnoDto.class)))
            .thenThrow(new EntidadDuplicadaException("Alumno", "DNI", "87654321"));
        
        // Ejecución y verificación
        mockMvc.perform(put("/alumno/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }
    
    @Test
    void actualizar_debeLanzarExcepcion_cuandoCarreraNoExiste() throws Exception {
        // Preparación
        Long idActualizar = 1L;
        
        AlumnoDto alumnoDto = new AlumnoDto();
        alumnoDto.setNombre("Tomas");
        alumnoDto.setApellido("Aguirrezabala");
        alumnoDto.setDni("12345678");
        alumnoDto.setCarreraId(999L); // ID de carrera inexistente
        
        // Mock del servicio - simula un error de carrera no encontrada
        when(alumnoService.guardar(any(AlumnoDto.class)))
            .thenThrow(new EntidadNoEncontradaException("Carrera", 999L));
        
        // Ejecución y verificación
        mockMvc.perform(put("/alumno/{id}", idActualizar)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnoDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje").exists());
        
        // Verificar que el servicio fue llamado
        verify(alumnoService, times(1)).guardar(any(AlumnoDto.class));
    }

    @Test
void eliminar_debeRetornarNoContent_cuandoSeEliminaCorrectamente() throws Exception {
    // Preparación
    Long idEliminar = 1L;
    
    // Mock del servicio - configurar para que no haga nada (éxito)
    doNothing().when(alumnoService).eliminarPorId(idEliminar);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/alumno/{id}", idEliminar)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).eliminarPorId(idEliminar);
}

@Test
void eliminar_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
    // Preparación
    Long idInexistente = 999L;
    
    // Mock del servicio - simula un error de alumno no encontrado
    doThrow(new EntidadNoEncontradaException("Alumno", idInexistente))
        .when(alumnoService).eliminarPorId(idInexistente);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/alumno/{id}", idInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).eliminarPorId(idInexistente);
}

@Test
void eliminar_debeLanzarExcepcion_cuandoAlumnoTieneAsignaturasAsociadas() throws Exception {
    // Preparación
    Long idConAsignaturas = 1L;
    
    // Mock del servicio - simula un error de regla de negocio por asignaturas asociadas
    doThrow(new ReglaNegocioException("No se puede eliminar el alumno porque tiene asignaturas asociadas"))
        .when(alumnoService).eliminarPorId(idConAsignaturas);
    
    // Ejecución y verificación
    mockMvc.perform(delete("/alumno/{id}", idConAsignaturas)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.mensaje").value("No se puede eliminar el alumno porque tiene asignaturas asociadas"));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).eliminarPorId(idConAsignaturas);
}

@Test
void inscribirEnMateria_debeRetornarAsignatura_cuandoDatosValidos() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idMateria = 2L;
    
    // Crear alumno simulado
    Alumno alumno = new Alumno();
    alumno.setId(idAlumno);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    // Crear materia simulada
    Materia materia = new Materia();
    materia.setId(idMateria);
    materia.setNombre("Programación I");
    
    // Crear asignatura simulada
    Asignatura asignaturaCreada = new Asignatura();
    asignaturaCreada.setId(1L);
    asignaturaCreada.setAlumno(alumno);
    asignaturaCreada.setMateria(materia);
    asignaturaCreada.setEstado(EstadoAsignatura.CURSANDO);
    
    // Mock del servicio
    when(alumnoService.inscribirEnMateria(idAlumno, idMateria)).thenReturn(asignaturaCreada);
    
    // Ejecución y verificación
    mockMvc.perform(post("/alumno/{idAlumno}/materia/{idMateria}", idAlumno, idMateria)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.estado", is("CURSANDO")))
            .andExpect(jsonPath("$.alumno.id", is(1)))
            .andExpect(jsonPath("$.materia.id", is(2)));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).inscribirEnMateria(idAlumno, idMateria);
}

@Test
void inscribirEnMateria_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
    // Preparación
    Long idAlumnoInexistente = 999L;
    Long idMateria = 1L;
    
    // Mock del servicio - simula error cuando el alumno no existe
    when(alumnoService.inscribirEnMateria(idAlumnoInexistente, idMateria))
        .thenThrow(new EntidadNoEncontradaException("Alumno", idAlumnoInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(post("/alumno/{idAlumno}/materia/{idMateria}", idAlumnoInexistente, idMateria)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).inscribirEnMateria(idAlumnoInexistente, idMateria);
}

@Test
void inscribirEnMateria_debeLanzarExcepcion_cuandoMateriaNoExiste() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idMateriaInexistente = 999L;
    
    // Mock del servicio - simula error cuando la materia no existe
    when(alumnoService.inscribirEnMateria(idAlumno, idMateriaInexistente))
        .thenThrow(new EntidadNoEncontradaException("Materia", idMateriaInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(post("/alumno/{idAlumno}/materia/{idMateria}", idAlumno, idMateriaInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).inscribirEnMateria(idAlumno, idMateriaInexistente);
}

@Test
void inscribirEnMateria_debeLanzarExcepcion_cuandoYaEstaInscrito() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idMateria = 2L;
    
    // Mock del servicio - simula error de inscripción duplicada
    when(alumnoService.inscribirEnMateria(idAlumno, idMateria))
        .thenThrow(new EntidadDuplicadaException("El alumno ya está inscrito en esta materia"));
    
    // Ejecución y verificación
    mockMvc.perform(post("/alumno/{idAlumno}/materia/{idMateria}", idAlumno, idMateria)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.error", is("Conflict")))
            .andExpect(jsonPath("$.mensaje").value("El alumno ya está inscrito en esta materia"));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).inscribirEnMateria(idAlumno, idMateria);
}

@Test
void inscribirEnMateria_debeLanzarExcepcion_cuandoNoSePuedeInscribir() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idMateria = 2L;
    
    // Mock del servicio - simula error de regla de negocio
    when(alumnoService.inscribirEnMateria(idAlumno, idMateria))
        .thenThrow(new ReglaNegocioException("No se puede inscribir porque no cumple con las correlatividades"));
    
    // Ejecución y verificación
    mockMvc.perform(post("/alumno/{idAlumno}/materia/{idMateria}", idAlumno, idMateria)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.mensaje").value("No se puede inscribir porque no cumple con las correlatividades"));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).inscribirEnMateria(idAlumno, idMateria);
}

@Test
void cambiarEstadoAsignatura_debeRetornarAsignaturaActualizada_cuandoDatosValidos() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idAsignatura = 2L;
    EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
    
    // Crear alumno simulado
    Alumno alumno = new Alumno();
    alumno.setId(idAlumno);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    // Crear materia simulada
    Materia materia = new Materia();
    materia.setId(3L);
    materia.setNombre("Programación I");
    
    // Crear asignatura actualizada
    Asignatura asignaturaActualizada = new Asignatura();
    asignaturaActualizada.setId(idAsignatura);
    asignaturaActualizada.setAlumno(alumno);
    asignaturaActualizada.setMateria(materia);
    asignaturaActualizada.setEstado(nuevoEstado);
    asignaturaActualizada.setNota(8.0);
    
    // Mock del servicio
    when(alumnoService.cambiarEstadoAsignatura(idAlumno, idAsignatura, nuevoEstado))
        .thenReturn(asignaturaActualizada);
    
    // Ejecución y verificación
    mockMvc.perform(put("/alumno/{idAlumno}/asignatura/{idAsignatura}", idAlumno, idAsignatura)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nuevoEstado)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.estado", is("APROBADO")))
            .andExpect(jsonPath("$.alumno.id", is(1)))
            .andExpect(jsonPath("$.materia.id", is(3)))
            .andExpect(jsonPath("$.nota", is(8.0)));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).cambiarEstadoAsignatura(idAlumno, idAsignatura, nuevoEstado);
}

@Test
void cambiarEstadoAsignatura_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
    // Preparación
    Long idAlumnoInexistente = 999L;
    Long idAsignatura = 2L;
    EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
    
    // Mock del servicio
    when(alumnoService.cambiarEstadoAsignatura(idAlumnoInexistente, idAsignatura, nuevoEstado))
        .thenThrow(new EntidadNoEncontradaException("Alumno", idAlumnoInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(put("/alumno/{idAlumno}/asignatura/{idAsignatura}", idAlumnoInexistente, idAsignatura)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nuevoEstado)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).cambiarEstadoAsignatura(idAlumnoInexistente, idAsignatura, nuevoEstado);
}

@Test
void cambiarEstadoAsignatura_debeLanzarExcepcion_cuandoAsignaturaNoExiste() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idAsignaturaInexistente = 999L;
    EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
    
    // Mock del servicio
    when(alumnoService.cambiarEstadoAsignatura(idAlumno, idAsignaturaInexistente, nuevoEstado))
        .thenThrow(new EntidadNoEncontradaException("Asignatura", idAsignaturaInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(put("/alumno/{idAlumno}/asignatura/{idAsignatura}", idAlumno, idAsignaturaInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nuevoEstado)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).cambiarEstadoAsignatura(idAlumno, idAsignaturaInexistente, nuevoEstado);
}

@Test
void cambiarEstadoAsignatura_debeLanzarExcepcion_cuandoAsignaturaNoPertenece() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    Long idAsignatura = 2L;
    EstadoAsignatura nuevoEstado = EstadoAsignatura.APROBADO;
    
    // Mock del servicio - simula error cuando la asignatura no pertenece al alumno
    when(alumnoService.cambiarEstadoAsignatura(idAlumno, idAsignatura, nuevoEstado))
        .thenThrow(new ReglaNegocioException("La asignatura no pertenece al alumno especificado"));
    
    // Ejecución y verificación
    mockMvc.perform(put("/alumno/{idAlumno}/asignatura/{idAsignatura}", idAlumno, idAsignatura)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(nuevoEstado)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Bad Request")))
            .andExpect(jsonPath("$.mensaje").value("La asignatura no pertenece al alumno especificado"));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).cambiarEstadoAsignatura(idAlumno, idAsignatura, nuevoEstado);
}

@Test
void obtenerAsignaturas_debeRetornarListaDeAsignaturas_cuandoAlumnoExiste() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    
    // Crear alumno simulado
    Alumno alumno = new Alumno();
    alumno.setId(idAlumno);
    alumno.setNombre("Tomas");
    alumno.setApellido("Aguirrezabala");
    
    // Crear materias simuladas
    Materia materia1 = new Materia();
    materia1.setId(1L);
    materia1.setNombre("Programación I");
    
    Materia materia2 = new Materia();
    materia2.setId(2L);
    materia2.setNombre("Matemática");
    
    // Crear asignaturas simuladas
    Asignatura asignatura1 = new Asignatura();
    asignatura1.setId(1L);
    asignatura1.setAlumno(alumno);
    asignatura1.setMateria(materia1);
    asignatura1.setEstado(EstadoAsignatura.CURSANDO);
    
    Asignatura asignatura2 = new Asignatura();
    asignatura2.setId(2L);
    asignatura2.setAlumno(alumno);
    asignatura2.setMateria(materia2);
    asignatura2.setEstado(EstadoAsignatura.APROBADO);
    asignatura2.setNota(9.0);
    
    List<Asignatura> asignaturas = Arrays.asList(asignatura1, asignatura2);
    
    // Mock del servicio
    when(alumnoService.obtenerAsignaturas(idAlumno)).thenReturn(asignaturas);
    
    // Ejecución y verificación
    mockMvc.perform(get("/alumno/{idAlumno}/asignaturas", idAlumno)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].estado", is("CURSANDO")))
            .andExpect(jsonPath("$[0].materia.nombre", is("Programación I")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].estado", is("APROBADO")))
            .andExpect(jsonPath("$[1].nota", is(9.0)))
            .andExpect(jsonPath("$[1].materia.nombre", is("Matemática")));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).obtenerAsignaturas(idAlumno);
}

@Test
void obtenerAsignaturas_debeRetornarListaVacia_cuandoAlumnoNoTieneAsignaturas() throws Exception {
    // Preparación
    Long idAlumno = 1L;
    
    // Mock del servicio
    when(alumnoService.obtenerAsignaturas(idAlumno)).thenReturn(Collections.emptyList());
    
    // Ejecución y verificación
    mockMvc.perform(get("/alumno/{idAlumno}/asignaturas", idAlumno)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).obtenerAsignaturas(idAlumno);
}

@Test
void obtenerAsignaturas_debeLanzarExcepcion_cuandoAlumnoNoExiste() throws Exception {
    // Preparación
    Long idAlumnoInexistente = 999L;
    
    // Mock del servicio
    when(alumnoService.obtenerAsignaturas(idAlumnoInexistente))
        .thenThrow(new EntidadNoEncontradaException("Alumno", idAlumnoInexistente));
    
    // Ejecución y verificación
    mockMvc.perform(get("/alumno/{idAlumno}/asignaturas", idAlumnoInexistente)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Not Found")))
            .andExpect(jsonPath("$.mensaje").exists());
    
    // Verificar que el servicio fue llamado
    verify(alumnoService, times(1)).obtenerAsignaturas(idAlumnoInexistente);
}
}