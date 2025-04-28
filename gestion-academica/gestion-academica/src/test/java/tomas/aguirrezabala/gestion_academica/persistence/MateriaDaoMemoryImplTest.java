package tomas.aguirrezabala.gestion_academica.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.persistence.impl.MateriaDaoMemoryImpl;

public class MateriaDaoMemoryImplTest {
    
    private MateriaDaoMemoryImpl materiaDao;
    private Profesor profesor;
    
    @BeforeEach
    void setUp() {
        materiaDao = new MateriaDaoMemoryImpl();
        
        // Crear profesor para las pruebas
        profesor = new Profesor();
        profesor.setId(1L);
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");
    }
    
    @Test
    void guardar_debeAsignarId_cuandoMateriaNueva() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación I");
        materia.setAnio(1);
        materia.setCuatrimestre(1);
        materia.setProfesor(profesor);
        
        // Ejecución
        Materia materiaGuardada = materiaDao.guardar(materia);
        
        // Verificación
        assertNotNull(materiaGuardada.getId());
        assertEquals("Programación I", materiaGuardada.getNombre());
        assertEquals(1, materiaGuardada.getAnio());
        assertEquals(1, materiaGuardada.getCuatrimestre());
        assertEquals(profesor, materiaGuardada.getProfesor());
    }
    
    @Test
    void guardar_debeActualizar_cuandoMateriaExistente() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación I");
        materia.setAnio(1);
        materia.setCuatrimestre(1);
        
        Materia materiaGuardada = materiaDao.guardar(materia);
        Long id = materiaGuardada.getId();
        
        // Actualización
        materiaGuardada.setNombre("Programación I - Actualizada");
        materiaGuardada.setAnio(2);
        materiaGuardada.setProfesor(profesor);
        materiaDao.guardar(materiaGuardada);
        
        // Verificación
        Optional<Materia> recuperada = materiaDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals("Programación I - Actualizada", recuperada.get().getNombre());
        assertEquals(2, recuperada.get().getAnio());
        assertEquals(profesor, recuperada.get().getProfesor());
    }
    
    @Test
    void buscarPorId_debeRetornarMateria_cuandoExisteId() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación I");
        materia.setAnio(1);
        materia.setCuatrimestre(1);
        materia.setProfesor(profesor);
        
        // Agregar correlatividades
        materia.setCorrelatividades(Arrays.asList(10L, 20L));
        
        Materia materiaGuardada = materiaDao.guardar(materia);
        Long id = materiaGuardada.getId();
        
        // Ejecución
        Optional<Materia> resultado = materiaDao.buscarPorId(id);
        
        // Verificación
        assertTrue(resultado.isPresent());
        assertEquals("Programación I", resultado.get().getNombre());
        assertEquals(1, resultado.get().getAnio());
        assertEquals(1, resultado.get().getCuatrimestre());
        assertEquals(profesor, resultado.get().getProfesor());
        assertEquals(2, resultado.get().getCorrelatividades().size());
        assertEquals(10L, resultado.get().getCorrelatividades().get(0));
        assertEquals(20L, resultado.get().getCorrelatividades().get(1));
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {
        // Ejecución y verificación
        assertFalse(materiaDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarAll_debeRetornarListaVacia_cuandoNoHayMaterias() {
        // Ejecución y verificación
        assertTrue(materiaDao.buscarAll().isEmpty());
    }
    
    @Test
    void buscarAll_debeRetornarTodasLasMaterias_cuandoHayMaterias() {
        // Preparación
        Materia materia1 = new Materia();
        materia1.setNombre("Programación I");
        materia1.setAnio(1);
        materia1.setCuatrimestre(1);
        
        Materia materia2 = new Materia();
        materia2.setNombre("Base de Datos");
        materia2.setAnio(2);
        materia2.setCuatrimestre(1);
        
        materiaDao.guardar(materia1);
        materiaDao.guardar(materia2);
        
        // Ejecución
        List<Materia> materias = materiaDao.buscarAll();
        
        // Verificación
        assertEquals(2, materias.size());
    }
    
    @Test
    void borrarPorId_debeEliminarMateria_cuandoExisteId() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación I");
        materia.setAnio(1);
        materia.setCuatrimestre(1);
        
        Materia materiaGuardada = materiaDao.guardar(materia);
        Long id = materiaGuardada.getId();
        
        // Verificar que existe antes de borrar
        assertTrue(materiaDao.buscarPorId(id).isPresent());
        
        // Ejecución
        materiaDao.borrarPorId(id);
        
        // Verificación
        assertFalse(materiaDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void borrarPorId_noDebeHacerNada_cuandoIdNoExiste() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación I");
        materia.setAnio(1);
        materia.setCuatrimestre(1);
        
        materiaDao.guardar(materia);
        int cantidadAntes = materiaDao.buscarAll().size();
        
        // Ejecución
        materiaDao.borrarPorId(999L);
        
        // Verificación
        assertEquals(cantidadAntes, materiaDao.buscarAll().size());
    }
    
    @Test
    void guardar_debePreservarCorrelatividades_cuandoSeActualizaMateria() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación II");
        materia.setAnio(2);
        materia.setCuatrimestre(1);
        
        // Agregar correlatividades iniciales
        materia.setCorrelatividades(Arrays.asList(1L, 2L));
        
        // Guardar materia con correlatividades
        Materia materiaGuardada = materiaDao.guardar(materia);
        Long id = materiaGuardada.getId();
        
        // Modificar nombre, pero mantener las correlatividades
        materiaGuardada.setNombre("Programación II - Actualizada");
        
        // Guardar los cambios
        materiaDao.guardar(materiaGuardada);
        
        // Verificación
        Optional<Materia> recuperada = materiaDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals("Programación II - Actualizada", recuperada.get().getNombre());
        assertEquals(2, recuperada.get().getCorrelatividades().size());
        assertEquals(1L, recuperada.get().getCorrelatividades().get(0));
        assertEquals(2L, recuperada.get().getCorrelatividades().get(1));
    }
    
    @Test
    void guardar_debeActualizarCorrelatividades_cuandoSeModifican() {
        // Preparación
        Materia materia = new Materia();
        materia.setNombre("Programación II");
        materia.setAnio(2);
        materia.setCuatrimestre(1);
        materia.setCorrelatividades(Arrays.asList(1L, 2L));
        
        // Guardar materia con correlatividades iniciales
        Materia materiaGuardada = materiaDao.guardar(materia);
        Long id = materiaGuardada.getId();
        
        // Modificar correlatividades
        materiaGuardada.setCorrelatividades(Arrays.asList(3L, 4L, 5L));
        
        // Guardar los cambios
        materiaDao.guardar(materiaGuardada);
        
        // Verificación
        Optional<Materia> recuperada = materiaDao.buscarPorId(id);
        assertTrue(recuperada.isPresent());
        assertEquals(3, recuperada.get().getCorrelatividades().size());
        assertEquals(3L, recuperada.get().getCorrelatividades().get(0));
        assertEquals(4L, recuperada.get().getCorrelatividades().get(1));
        assertEquals(5L, recuperada.get().getCorrelatividades().get(2));
    }
    
    @Test
    void guardar_debeAsignarIdsConsecutivos_cuandoMultiplesMaterias() {
        // Preparación
        Materia materia1 = new Materia();
        materia1.setNombre("Programación I");
        
        Materia materia2 = new Materia();
        materia2.setNombre("Base de Datos");
        
        Materia materia3 = new Materia();
        materia3.setNombre("Sistemas Operativos");
        
        // Ejecución
        Materia materiaGuardada1 = materiaDao.guardar(materia1);
        Materia materiaGuardada2 = materiaDao.guardar(materia2);
        Materia materiaGuardada3 = materiaDao.guardar(materia3);
        
        // Verificación de IDs consecutivos
        assertEquals(1, materiaGuardada2.getId() - materiaGuardada1.getId());
        assertEquals(1, materiaGuardada3.getId() - materiaGuardada2.getId());
    }
}