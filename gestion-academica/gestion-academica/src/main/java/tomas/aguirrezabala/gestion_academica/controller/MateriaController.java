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

import tomas.aguirrezabala.gestion_academica.business.MateriaService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Materia;
import tomas.aguirrezabala.gestion_academica.model.dto.MateriaDto;

@RestController
@RequestMapping("/materia")
public class MateriaController {
    
    @Autowired
    private MateriaService materiaService;

    @GetMapping
    public ResponseEntity<List<Materia>> listarTodas() {
        List<Materia> materias = materiaService.buscarTodas();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materia> buscarPorId(@PathVariable Long id) {
        return materiaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntidadNoEncontradaException("Materia", id));
    }

    @PostMapping
    public ResponseEntity<Materia> crear(@RequestBody MateriaDto materiaDto) {
        Materia materiaCreada = materiaService.guardar(materiaDto);
        return new ResponseEntity<>(materiaCreada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> actualizar(@PathVariable Long id, @RequestBody MateriaDto materiaDto) {
        materiaDto.setId(id);
        Materia materiaActualizada = materiaService.guardar(materiaDto);
        return ResponseEntity.ok(materiaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        materiaService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/con-correlatividades")
    public ResponseEntity<Materia> crearConCorrelatividades(@RequestBody MateriaDto materiaDto) {

        Materia materiaGuardada = materiaService.guardar(materiaDto);

        if (materiaDto.getCorrelatividades() != null && !materiaDto.getCorrelatividades().isEmpty()) {
            materiaGuardada = materiaService.crearConCorrelatividades(materiaGuardada, materiaDto.getCorrelatividades());
        }
        
        return new ResponseEntity<>(materiaGuardada, HttpStatus.CREATED);
    }
    
    @PostMapping("/{id}/correlatividades")
    public ResponseEntity<Materia> agregarCorrelatividades(
            @PathVariable Long id, 
            @RequestBody List<Long> correlatividades) {

        Materia materia = materiaService.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Materia", id));

        materia = materiaService.crearConCorrelatividades(materia, correlatividades);
        
        return ResponseEntity.ok(materia);
    }
}