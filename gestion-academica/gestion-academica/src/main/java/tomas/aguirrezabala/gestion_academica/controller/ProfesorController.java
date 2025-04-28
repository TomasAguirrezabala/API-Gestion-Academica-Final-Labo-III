package tomas.aguirrezabala.gestion_academica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tomas.aguirrezabala.gestion_academica.business.ProfesorService;
import tomas.aguirrezabala.gestion_academica.business.impl.ProfesorServiceImpl;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.Profesor;
import tomas.aguirrezabala.gestion_academica.model.dto.ProfesorDto;

@RestController
@RequestMapping("/profesor")
public class ProfesorController {
    
    @Autowired
    private ProfesorService profesorService;
    
    @Autowired
    private ProfesorServiceImpl profesorServiceImpl; 
    
    @GetMapping
    public ResponseEntity<List<Profesor>> listarTodos() {
        List<Profesor> profesores = profesorService.buscarTodos();
        return ResponseEntity.ok(profesores);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Profesor> buscarPorId(@PathVariable Long id) {
        return profesorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntidadNoEncontradaException("Profesor", id));
    }

    @PostMapping
    public ResponseEntity<Profesor> crear(@RequestBody ProfesorDto profesorDto) {
        Profesor profesorCreado = profesorService.guardar(profesorDto);
        return new ResponseEntity<>(profesorCreado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profesor> actualizar(@PathVariable Long id, @RequestBody ProfesorDto profesorDto) {
        profesorDto.setId(id);
        Profesor profesorActualizado = profesorService.guardar(profesorDto);
        return ResponseEntity.ok(profesorActualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        profesorService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{profesorId}/materias")
    public ResponseEntity<List<Materia>> obtenerMateriasOrdenadas(@PathVariable Long profesorId) {
        List<Materia> materias = profesorServiceImpl.obtenerMateriasOrdenadas(profesorId);
        return ResponseEntity.ok(materias);
    }

    @PostMapping("/{id}/materias")
    public ResponseEntity<Profesor> asignarMaterias(
            @PathVariable Long id, 
            @RequestBody ProfesorDto profesorDto) {

        Profesor profesor = profesorService.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Profesor", id));

        profesorDto.setId(id);
        profesorDto.setNombre(profesor.getNombre());
        profesorDto.setApellido(profesor.getApellido());
        profesorDto.setTitulo(profesor.getTitulo());

        Profesor profesorActualizado = profesorService.guardar(profesorDto);
        
        return ResponseEntity.ok(profesorActualizado);
    }
}