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

import tomas.aguirrezabala.gestion_academica.business.CarreraService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Carrera;
import tomas.aguirrezabala.gestion_academica.model.dto.CarreraDto;

@RestController
@RequestMapping("/carrera")
public class CarreraController {
    
    @Autowired
    private CarreraService carreraService;
    
   
    @GetMapping
    public ResponseEntity<List<Carrera>> listarTodas() {
        List<Carrera> carreras = carreraService.buscarTodas();
        return ResponseEntity.ok(carreras);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Carrera> buscarPorId(@PathVariable Long id) {
        return carreraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntidadNoEncontradaException("Carrera", id));
    }
    
    @PostMapping
    public ResponseEntity<Carrera> crear(@RequestBody CarreraDto carreraDto) {
        Carrera carreraCreada = carreraService.guardar(carreraDto);
        return new ResponseEntity<>(carreraCreada, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Carrera> actualizar(@PathVariable Long id, @RequestBody CarreraDto carreraDto) {
        carreraDto.setId(id);
        Carrera carreraActualizada = carreraService.guardar(carreraDto);
        return ResponseEntity.ok(carreraActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        carreraService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{carreraId}/materia/{materiaId}")
    public ResponseEntity<Carrera> agregarMateria(
            @PathVariable Long carreraId,
            @PathVariable Long materiaId) {
        
        Carrera carreraActualizada = carreraService.agregarMateria(carreraId, materiaId);
        return ResponseEntity.ok(carreraActualizada);
    }

    @GetMapping("/{carreraId}/materias")
    public ResponseEntity<List<tomas.aguirrezabala.gestion_academica.model.Materia>> obtenerMaterias(
            @PathVariable Long carreraId) {

        Carrera carrera = carreraService.buscarPorId(carreraId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Carrera", carreraId));
        
        return ResponseEntity.ok(carrera.getMaterias());
    }
}