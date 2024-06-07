package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.models.Curriculum;
import ak.spring.services.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/curricula")
@CrossOrigin(origins =  "http://localhost:5173")
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping
    public List<CurriculumDTO> getAllCurricula() {
        return curriculumService.getAllCurricula();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurriculumDTO> getCurriculumById(@PathVariable int id) {
        return ResponseEntity.ok(curriculumService.getCurriculumById(id));
    }

    @PostMapping
    public CurriculumDTO createCurriculum(@RequestBody Curriculum curriculum) {
        return curriculumService.saveCurriculum(curriculum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurriculumDTO> updateCurriculum(@PathVariable int id, @RequestBody Curriculum curriculumDetails) {
        return ResponseEntity.ok(curriculumService.updateCurriculum(id, curriculumDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable int id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getCourseById(@PathVariable int id) {
        return ResponseEntity.ok(curriculumService.getCourseById(id));
    }

    @GetMapping("/{curriculumId}/classrooms")
    public ResponseEntity<List<ClassroomDTO>> getClassroomById(@PathVariable int curriculumId) {
        return ResponseEntity.ok(curriculumService.getClassroomById(curriculumId));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<CurriculumDTO>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(curriculumService.findByName(name));
    }
}
