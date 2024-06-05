package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Classroom;
import ak.spring.models.Person;
import ak.spring.services.ClassroomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins =  "http://localhost:8080")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Classroom>> findByName(@PathVariable String name) {
        List<Classroom> classrooms = classroomService.findByName(name);
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Classroom>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<Classroom> groups = classroomService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> findById(@PathVariable int id) {
        return ResponseEntity.ok(classroomService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classroom> updateGroup(@PathVariable int id, @Valid @RequestBody Classroom updatedClassroom) {
        Classroom classroom = classroomService.updateGroup(id, updatedClassroom);
        return ResponseEntity.ok(classroom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable int id) {
        classroomService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Classroom> uploadGroup(@Valid @RequestBody Classroom classroom) {
        Classroom savedClassroom = classroomService.uploadGroup(classroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClassroom);
    }

    @GetMapping
    public ResponseEntity<List<Classroom>> findAll() {
        List<Classroom> classrooms = classroomService.findAll();
        return ResponseEntity.ok(classrooms);
    }

//    @GetMapping("/{id}/teacher")
//    public ResponseEntity<PersonDTO> getTeacher(@PathVariable int id) {
//        PersonDTO teacher = classroomService.getTeacher(id);
//        return ResponseEntity.ok(teacher);
//    }
//
//    @GetMapping("/{id}/students")
//    public ResponseEntity<List<PersonDTO>> getStudents(@PathVariable int id) {
//        List<PersonDTO> students = classroomService.getStudents(id);
//        return ResponseEntity.ok(students);
//    }
}