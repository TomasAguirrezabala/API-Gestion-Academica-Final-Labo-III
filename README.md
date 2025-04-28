Sistema de Gestión Académica
Este proyecto implementa una API REST para la gestión académica de una institución educativa,
permitiendo administrar carreras, materias, profesores, alumnos e inscripciones.

Características

Gestión de carreras con sus planes de estudio
Administración de materias con sistema de correlatividades
Registro y seguimiento de profesores
Control de alumnos e inscripciones a materias
Validación de correlatividades para inscripción a materias
Seguimiento de calificaciones y estados académicos


Tecnologías utilizadas

Java 11
Spring Boot 2.7
Base de datos en memoria (H2)
Maven

Endpoints principales

Carreras
GET /carrera - Listar todas las carreras
GET /carrera/{id} - Obtener carrera por ID
POST /carrera - Crear nueva carrera
PUT /carrera/{id} - Actualizar carrera existente
POST /carrera/{id}/materia/{id} - Asignar materia a carrera

Profesores
GET /profesor - Listar todos los profesores
GET /profesor/{id} - Obtener profesor por ID
GET /profesor/{id}/materias - Obtener materias de un profesor
POST /profesor - Crear nuevo profesor
PUT /profesor/{id} - Actualizar profesor existente

Materias
GET /materia - Listar todas las materias
GET /materia/{id} - Obtener materia por ID
POST /materia - Crear nueva materia
POST /materia/con-correlatividades - Crear materia con correlatividades
POST /materia/{id}/correlatividades - Asignar correlatividades a materia existente

Alumnos
GET /alumno - Listar todos los alumnos
GET /alumno/{id} - Obtener alumno por ID
GET /alumno/{id}/asignaturas - Obtener inscripciones de un alumno
POST /alumno - Registrar nuevo alumno
POST /alumno/{id}/materia/{id} - Inscribir alumno a materia

Asignaturas (Inscripciones)
GET /asignatura - Listar todas las inscripciones
POST /asignatura - Crear inscripción
PUT /asignatura/{id}/estado - Actualizar estado de inscripción
PUT /asignatura/{id}/nota - Asignar nota a inscripción

Uso con Postman
Este proyecto incluye una colección de Postman para facilitar las pruebas. 
Para utilizarla:

Importar el archivo Gestion Academica API.postman_collection
Ejecutar la aplicación Spring Boot
Ejecutar las peticiones de Postman en el orden sugerido:
Crear carreras
Crear profesores
Crear materias
Asignar correlatividades
Registrar alumnos
Inscribir alumnos a materias
Actualizar estados y notas

Reglas de negocio implementadas

Un alumno no puede inscribirse a una materia si no cumple con las correlatividades
Las correlatividades se consideran cumplidas si la materia está en estado APROBADO o REGULAR
Las materias con alumnos inscriptos o que son correlativas de otras no pueden eliminarse
No se pueden crear ciclos en el sistema de correlatividades

Ejecución del proyecto
La aplicación estará disponible en http://localhost:8080