package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.persistence.impl.CarreraDaoMemoryImpl;

public class CarreraDaoMemoryImplTest {
    
    private CarreraDaoMemoryImpl carreraDao;
    
    @BeforeEach
    void setUp() {
        carreraDao = new CarreraDaoMemoryImpl();
    }
    
    @Test
    void guardar_debeAsignarId_cuandoCarreraNueva() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        // Ejecución
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        
        // Verificación
        assertNotNull(carreraGuardada.getId());
        assertEquals("Técnico Universitario en Programación", carreraGuardada.getNombre());
        assertEquals(2, carreraGuardada.getDuracionAnios());
    }
    
    @Test
    void guardar_debeActualizar_cuandoCarreraExistente() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        Long id = carreraGuardada.getId();
        
        // Actualización
        carreraGuardada.setNombre("Técnico Superior en Programación");
        carreraGuardada.setDuracionAnios(3);
        carreraDao.guardar(carreraGuardada);
        
        // Verificación
        Optional<Carrera> recuperada = carreraDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals("Técnico Superior en Programación", recuperada.get().getNombre());
        assertEquals(3, recuperada.get().getDuracionAnios());
    }
    
    @Test
    void buscarPorId_debeRetornarCarrera_cuandoExisteId() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        // Agregar algunas materias a la carrera
        Materia materia = new Materia();
        materia.setId(1L);
        materia.setNombre("Programación I");
        carrera.getMaterias().add(materia);
        
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        Long id = carreraGuardada.getId();
        
        // Ejecución
        Optional<Carrera> resultado = carreraDao.buscarPorId(id);
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("Técnico Universitario en Programación", resultado.get().getNombre());
        assertEquals(2, resultado.get().getDuracionAnios());
        assertEquals(1, resultado.get().getMaterias().size());
        assertEquals("Programación I", resultado.get().getMaterias().get(0).getNombre());
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {
        // Ejecución y verificación
        assertFalse(carreraDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarAll_debeRetornarListaVacia_cuandoNoHayCarreras() {
        // Ejecución y verificación
        assertTrue(carreraDao.buscarAll().isEmpty());
    }
    
    @Test
    void buscarAll_debeRetornarTodasLasCarreras_cuandoHayCarreras() {
        // Preparación
        Carrera carrera1 = new Carrera();
        carrera1.setNombre("Técnico Universitario en Programación");
        carrera1.setDuracionAnios(2);
        
        Carrera carrera2 = new Carrera();
        carrera2.setNombre("Ingeniería en Sistemas");
        carrera2.setDuracionAnios(5);
        
        carreraDao.guardar(carrera1);
        carreraDao.guardar(carrera2);
        
        // Ejecución
        List<Carrera> carreras = carreraDao.buscarAll();
        
        // Verificación
        assertEquals(2, carreras.size());
    }
    
    @Test
    void borrarPorId_debeEliminarCarrera_cuandoExisteId() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        Long id = carreraGuardada.getId();
        
        // Verificar que existe antes de borrar
        assertTrue(carreraDao.buscarPorId(id).isPresent());
        
        // Ejecución
        carreraDao.borrarPorId(id);
        
        // Verificación
        assertFalse(carreraDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void borrarPorId_noDebeHacerNada_cuandoIdNoExiste() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        carreraDao.guardar(carrera);
        int cantidadAntes = carreraDao.buscarAll().size();
        
        // Ejecución
        carreraDao.borrarPorId(999L);
        
        // Verificación
        assertEquals(cantidadAntes, carreraDao.buscarAll().size());
    }
    
    @Test
    void guardar_debePreservarMaterias_cuandoSeActualizaCarrera() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        // Agregar materias iniciales
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        carrera.getMaterias().add(materia1);
        
        // Guardar carrera con materias
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        Long id = carreraGuardada.getId();
        
        // Modificar nombre y duración, pero mantener las materias
        carreraGuardada.setNombre("Técnico Superior en Programación");
        carreraGuardada.setDuracionAnios(3);
        
        // Guardar los cambios
        carreraDao.guardar(carreraGuardada);
        
        // Verificación
        Optional<Carrera> recuperada = carreraDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals("Técnico Superior en Programación", recuperada.get().getNombre());
        assertEquals(3, recuperada.get().getDuracionAnios());
        assertEquals(1, recuperada.get().getMaterias().size());
        assertEquals("Programación I", recuperada.get().getMaterias().get(0).getNombre());
    }
    
    @Test
    void guardar_debeAgregarMaterias_cuandoSeActualizaConNuevasMaterias() {
        // Preparación
        Carrera carrera = new Carrera();
        carrera.setNombre("Técnico Universitario en Programación");
        carrera.setDuracionAnios(2);
        
        // Guardar carrera sin materias
        Carrera carreraGuardada = carreraDao.guardar(carrera);
        Long id = carreraGuardada.getId();
        
        // Añadir materias
        Materia materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        
        carreraGuardada.getMaterias().add(materia1);
        carreraGuardada.getMaterias().add(materia2);
        
        // Guardar los cambios
        carreraDao.guardar(carreraGuardada);
        
        // Verificación
        Optional<Carrera> recuperada = carreraDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals(2, recuperada.get().getMaterias().size());
    }
}