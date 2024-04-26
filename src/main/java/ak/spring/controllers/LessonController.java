package ak.spring.controllers;

import ak.spring.models.Lesson;
import ak.spring.services.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins =  "http://localhost:8080")
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Lesson>> findByName(@PathVariable String name) {
        List<Lesson> lessons = lessonService.findByName(name);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Lesson>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<Lesson> lessons = lessonService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> findById(@PathVariable int id) {
        Lesson lesson = lessonService.findById(id);
        return ResponseEntity.ok(lesson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable int id) {
        Lesson lesson = lessonService.findById(id);
        lessonService.deleteLesson(lesson);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Lesson> saveLesson(@Valid @RequestBody Lesson lesson) {
        Lesson savedLesson = lessonService.saveLesson(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable int id, @Valid @RequestBody Lesson updatedLesson) {
        Lesson lesson = lessonService.updateLesson(id, updatedLesson);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping
    public ResponseEntity<List<Lesson>> findAll() {
        List<Lesson> lessons = lessonService.findAll();
        return ResponseEntity.ok(lessons);
    }
}