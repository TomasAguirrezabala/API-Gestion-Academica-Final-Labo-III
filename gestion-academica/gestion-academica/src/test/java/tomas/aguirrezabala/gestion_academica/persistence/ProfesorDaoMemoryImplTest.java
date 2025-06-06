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
import tomas.aguirrezabala.gestion_academica.persistence.impl.ProfesorDaoMemoryImpl;

public class ProfesorDaoMemoryImplTest {
    
    private ProfesorDaoMemoryImpl profesorDao;
    private Materia materia1, materia2;
    
    @BeforeEach
    void setUp() {
        profesorDao = new ProfesorDaoMemoryImpl();

        materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNombre("Programación I");
        materia1.setAnio(1);
        materia1.setCuatrimestre(1);
        
        materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNombre("Base de Datos");
        materia2.setAnio(1);
        materia2.setCuatrimestre(2);
    }
    
    @Test
    void guardar_debeAsignarId_cuandoProfesorNuevo() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");

        Profesor profesorGuardado = profesorDao.guardar(profesor);

        assertNotNull(profesorGuardado.getId());
        assertEquals("Tomas", profesorGuardado.getNombre());
        assertEquals("Aguirrezabala", profesorGuardado.getApellido());
        assertEquals("Licenciado en Informática", profesorGuardado.getTitulo());
    }
    
    @Test
    void guardar_debeActualizar_cuandoProfesorExistente() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");
        
        Profesor profesorGuardado = profesorDao.guardar(profesor);
        Long id = profesorGuardado.getId();

        profesorGuardado.setTitulo("Doctor en Ciencias de la Computación");
        profesorDao.guardar(profesorGuardado);

        Optional<Profesor> recuperado = profesorDao.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals("Doctor en Ciencias de la Computación", recuperado.get().getTitulo());
    }
    
    @Test
    void buscarPorId_debeRetornarProfesor_cuandoExisteId() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");

        profesor.setMaterias(Arrays.asList(materia1, materia2));
        
        Profesor profesorGuardado = profesorDao.guardar(profesor);
        Long id = profesorGuardado.getId();

        Optional<Profesor> resultado = profesorDao.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("Tomas", resultado.get().getNombre());
        assertEquals("Aguirrezabala", resultado.get().getApellido());
        assertEquals("Licenciado en Informática", resultado.get().getTitulo());
        assertEquals(2, resultado.get().getMaterias().size());
        assertEquals("Programación I", resultado.get().getMaterias().get(0).getNombre());
        assertEquals("Base de Datos", resultado.get().getMaterias().get(1).getNombre());
    }
    
    @Test
    void buscarPorId_debeRetornarOptionalVacio_cuandoNoExisteId() {

        assertFalse(profesorDao.buscarPorId(999L).isPresent());
    }
    
    @Test
    void buscarAll_debeRetornarListaVacia_cuandoNoHayProfesores() {

        assertTrue(profesorDao.buscarAll().isEmpty());
    }
    
    @Test
    void buscarAll_debeRetornarTodosLosProfesores_cuandoHayProfesores() {

        Profesor profesor1 = new Profesor();
        profesor1.setNombre("Tomas");
        profesor1.setApellido("Aguirrezabala");
        profesor1.setTitulo("Licenciado en Informática");
        
        Profesor profesor2 = new Profesor();
        profesor2.setNombre("Juan");
        profesor2.setApellido("Pérez");
        profesor2.setTitulo("Ingeniero en Sistemas");
        
        profesorDao.guardar(profesor1);
        profesorDao.guardar(profesor2);

        List<Profesor> profesores = profesorDao.buscarAll();

        assertEquals(2, profesores.size());
    }
    
    @Test
    void borrarPorId_debeEliminarProfesor_cuandoExisteId() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");
        
        Profesor profesorGuardado = profesorDao.guardar(profesor);
        Long id = profesorGuardado.getId();

        assertTrue(profesorDao.buscarPorId(id).isPresent());

        profesorDao.borrarPorId(id);

        assertFalse(profesorDao.buscarPorId(id).isPresent());
    }
    
    @Test
    void borrarPorId_noDebeHacerNada_cuandoIdNoExiste() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");
        
        profesorDao.guardar(profesor);
        int cantidadAntes = profesorDao.buscarAll().size();

        profesorDao.borrarPorId(999L);

        assertEquals(cantidadAntes, profesorDao.buscarAll().size());
    }
    
    @Test
    void guardar_debePreservarMaterias_cuandoSeActualizaProfesor() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");
        profesor.setMaterias(Arrays.asList(materia1));

        Profesor profesorGuardado = profesorDao.guardar(profesor);
        Long id = profesorGuardado.getId();

        profesorGuardado.setTitulo("Doctor en Ciencias de la Computación");

        profesorDao.guardar(profesorGuardado);

        Optional<Profesor> recuperado = profesorDao.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals("Doctor en Ciencias de la Computación", recuperado.get().getTitulo());
        assertEquals(1, recuperado.get().getMaterias().size());
        assertEquals("Programación I", recuperado.get().getMaterias().get(0).getNombre());
    }
    
    @Test
    void guardar_debeAgregarMaterias_cuandoSeActualizaConNuevasMaterias() {

        Profesor profesor = new Profesor();
        profesor.setNombre("Tomas");
        profesor.setApellido("Aguirrezabala");
        profesor.setTitulo("Licenciado en Informática");

        Profesor profesorGuardado = profesorDao.guardar(profesor);
        Long id = profesorGuardado.getId();

        profesorGuardado.setMaterias(Arrays.asList(materia1, materia2));

        profesorDao.guardar(profesorGuardado);

        Optional<Profesor> recuperado = profesorDao.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        assertEquals(2, recuperado.get().getMaterias().size());
        assertEquals("Programación I", recuperado.get().getMaterias().get(0).getNombre());
        assertEquals("Base de Datos", recuperado.get().getMaterias().get(1).getNombre());
    }
    
    @Test
    void guardar_debeAsignarIdsConsecutivos_cuandoMultiplesProfesores() {

        Profesor profesor1 = new Profesor();
        profesor1.setNombre("Tomas");
        profesor1.setApellido("Aguirrezabala");
        
        Profesor profesor2 = new Profesor();
        profesor2.setNombre("Juan");
        profesor2.setApellido("Pérez");
        
        Profesor profesor3 = new Profesor();
        profesor3.setNombre("María");
        profesor3.setApellido("González");

        Profesor profesorGuardado1 = profesorDao.guardar(profesor1);
        Profesor profesorGuardado2 = profesorDao.guardar(profesor2);
        Profesor profesorGuardado3 = profesorDao.guardar(profesor3);

        assertEquals(1, profesorGuardado2.getId() - profesorGuardado1.getId());
        assertEquals(1, profesorGuardado3.getId() - profesorGuardado2.getId());
    }
}