package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Classroom;
import ak.spring.services.ClassroomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/groups")
@CrossOrigin(origins =  "http://localhost:5173")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ClassroomDTO>> findByName(@PathVariable String name) {
        List<ClassroomDTO> classrooms = classroomService.findByName(name);
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<ClassroomDTO>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<ClassroomDTO> groups = classroomService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassroomDTO> findById(@PathVariable int id) {
        ClassroomDTO classroom = classroomService.findById(id);
        return ResponseEntity.ok(classroom);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassroomDTO> updateGroup(@PathVariable int id, @Valid @RequestBody Classroom updatedClassroom) {
        ClassroomDTO classroom = classroomService.updateGroup(id, updatedClassroom);
        return ResponseEntity.ok(classroom);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable int id) {
        classroomService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ClassroomDTO> uploadGroup(@Valid @RequestBody Classroom classroom) {
        ClassroomDTO savedClassroom = classroomService.uploadGroup(classroom);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClassroom);
    }

    @GetMapping
    public ResponseEntity<List<ClassroomDTO>> findAll() {
        List<ClassroomDTO> classrooms = classroomService.findAll();
        return ResponseEntity.ok(classrooms);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<PersonDTO>> getStudents(@PathVariable int id) {
        List<PersonDTO> students = classroomService.getStudents(id);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<CurriculumDTO> getCurriculumById(@PathVariable int id) {
        CurriculumDTO curriculum = classroomService.getCurriculum(id);
        return ResponseEntity.ok(curriculum);
    }
}