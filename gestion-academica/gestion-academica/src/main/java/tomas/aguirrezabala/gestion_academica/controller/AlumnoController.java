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

import tomas.aguirrezabala.gestion_academica.business.AlumnoService;
import tomas.aguirrezabala.gestion_academica.exception.EntidadNoEncontradaException; 
import tomas.aguirrezabala.gestion_academica.model.Alumno;
import tomas.aguirrezabala.gestion_academica.model.Asignatura;
import tomas.aguirrezabala.gestion_academica.model.EstadoAsignatura;
import tomas.aguirrezabala.gestion_academica.model.dto.AlumnoDto;

@RestController
@RequestMapping("/alumno")
public class AlumnoController {
    
    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    public ResponseEntity<List<Alumno>> listarTodos() {
        List<Alumno> alumnos = alumnoService.buscarTodos();
        return ResponseEntity.ok(alumnos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> buscarPorId(@PathVariable Long id) {
        return alumnoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntidadNoEncontradaException("Alumno", id));
    }

    @PostMapping
    public ResponseEntity<Alumno> crear(@RequestBody AlumnoDto alumnoDto) {
        Alumno alumnoCreado = alumnoService.guardar(alumnoDto);
        return new ResponseEntity<>(alumnoCreado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> actualizar(@PathVariable Long id, @RequestBody AlumnoDto alumnoDto) {
        alumnoDto.setId(id);
        Alumno alumnoActualizado = alumnoService.guardar(alumnoDto);
        return ResponseEntity.ok(alumnoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alumnoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idAlumno}/materia/{idMateria}")
    public ResponseEntity<Asignatura> inscribirEnMateria(
            @PathVariable Long idAlumno, 
            @PathVariable Long idMateria) {
        Asignatura asignatura = alumnoService.inscribirEnMateria(idAlumno, idMateria);
        return new ResponseEntity<>(asignatura, HttpStatus.CREATED);
    }

    @PutMapping("/{idAlumno}/asignatura/{idAsignatura}")
    public ResponseEntity<Asignatura> cambiarEstadoAsignatura(
            @PathVariable Long idAlumno, 
            @PathVariable Long idAsignatura, 
            @RequestBody EstadoAsignatura estado) {
        Asignatura asignatura = alumnoService.cambiarEstadoAsignatura(idAlumno, idAsignatura, estado);
        return ResponseEntity.ok(asignatura);
    }

    @GetMapping("/{idAlumno}/asignaturas")
    public ResponseEntity<List<Asignatura>> obtenerAsignaturas(@PathVariable Long idAlumno) {
        List<Asignatura> asignaturas = alumnoService.obtenerAsignaturas(idAlumno);
        return ResponseEntity.ok(asignaturas);
    }
}