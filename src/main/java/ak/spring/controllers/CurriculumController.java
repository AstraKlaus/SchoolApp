package ak.spring.controllers;

import ak.spring.models.Curriculum;
import ak.spring.services.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curricula")
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping
    public List<Curriculum> getAllCurricula() {
        return curriculumService.getAllCurricula();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curriculum> getCurriculumById(@PathVariable int id) {
        return ResponseEntity.ok(curriculumService.getCurriculumById(id));
    }

    @PostMapping
    public Curriculum createCurriculum(@RequestBody Curriculum curriculum) {
        return curriculumService.saveCurriculum(curriculum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curriculum> updateCurriculum(@PathVariable int id, @RequestBody Curriculum curriculumDetails) {
        Curriculum curriculum = curriculumService.getCurriculumById(id);
        curriculum.setName(curriculumDetails.getName());
        curriculum.setDescription(curriculumDetails.getDescription());
        curriculum.setAccess(curriculumDetails.getAccess());

        return ResponseEntity.ok(curriculumService.saveCurriculum(curriculum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable int id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.noContent().build();
    }
}
