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

import tomas.aguirrezabala.gestion_academica.business.AsignaturaService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.dto.AsignaturaDto;

@RestController
@RequestMapping("/asignatura")
public class AsignaturaController {
    
    @Autowired
    private AsignaturaService asignaturaService;
    
    @GetMapping
    public ResponseEntity<List<Asignatura>> listarTodas() {
        List<Asignatura> asignaturas = asignaturaService.buscarTodas();
        return ResponseEntity.ok(asignaturas);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> buscarPorId(@PathVariable Long id) {
        return asignaturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntidadNoEncontradaException("Asignatura", id));
    }
    
    @PostMapping
    public ResponseEntity<Asignatura> crear(@RequestBody AsignaturaDto asignaturaDto) {
        Asignatura asignaturaCreada = asignaturaService.guardar(asignaturaDto);
        return new ResponseEntity<>(asignaturaCreada, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizar(@PathVariable Long id, @RequestBody AsignaturaDto asignaturaDto) {
        asignaturaDto.setId(id);
        Asignatura asignaturaActualizada = asignaturaService.guardar(asignaturaDto);
        return ResponseEntity.ok(asignaturaActualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asignaturaService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<Asignatura> actualizarEstado(
            @PathVariable Long id, 
            @RequestBody EstadoAsignatura estado) {

        Asignatura asignatura = asignaturaService.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Asignatura", id));

        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setId(id);
        asignaturaDto.setAlumnoId(asignatura.getAlumno().getId());
        asignaturaDto.setMateriaId(asignatura.getMateria().getId());
        asignaturaDto.setNota(asignatura.getNota());

        asignaturaDto.setEstado(estado);

        Asignatura asignaturaActualizada = asignaturaService.guardar(asignaturaDto);
        return ResponseEntity.ok(asignaturaActualizada);
    }
    
    @PutMapping("/{id}/nota")
    public ResponseEntity<Asignatura> actualizarNota(
            @PathVariable Long id, 
            @RequestBody Double nota) {

        Asignatura asignatura = asignaturaService.buscarPorId(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Asignatura", id));

        AsignaturaDto asignaturaDto = new AsignaturaDto();
        asignaturaDto.setId(id);
        asignaturaDto.setAlumnoId(asignatura.getAlumno().getId());
        asignaturaDto.setMateriaId(asignatura.getMateria().getId());
        asignaturaDto.setEstado(asignatura.getEstado());

        asignaturaDto.setNota(nota);

        if (nota != null && nota >= 7.0) {
            asignaturaDto.setEstado(EstadoAsignatura.APROBADO);
        }

        Asignatura asignaturaActualizada = asignaturaService.guardar(asignaturaDto);
        return ResponseEntity.ok(asignaturaActualizada);
    }
}