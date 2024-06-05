package ak.spring.controllers;

import ak.spring.dto.HomeworkDTO;
import ak.spring.models.Homework;
import ak.spring.services.HomeworkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequestMapping("/api/homeworks")
@CrossOrigin(origins = "http://localhost:8080")
public class HomeworkController {

    private final HomeworkService homeworkService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Homework>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(homeworkService.findByName(name));
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Homework>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        return ResponseEntity.ok(homeworkService.findWithPagination(offset, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Homework> findById(@PathVariable int id) {
        return ResponseEntity.ok(homeworkService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomework(@PathVariable int id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Homework> saveHomework(@Valid @RequestBody Homework homework) {
        Homework savedHomework = homeworkService.saveHomework(homework);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHomework);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Homework> updateHomework(@PathVariable int id, @Valid @RequestBody Homework updatedHomework) {
        Homework homework = homeworkService.updateHomework(id, updatedHomework);
        return ResponseEntity.ok(homework);
    }

    @GetMapping
    public ResponseEntity<List<Homework>> findAll() {
        return ResponseEntity.ok(homeworkService.findAll());
    }
}

