package ak.spring.controllers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Course;
import ak.spring.models.Person;
import ak.spring.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins =  "http://localhost:8080")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> findAll() {
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Course>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        return ResponseEntity.ok(courseService.findWithPagination(offset, pageSize));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Course>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(courseService.findByName(name));
    }

    @PostMapping
    public ResponseEntity<Course> uploadCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.uploadCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> findById(@PathVariable int id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable int id, @Valid @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(id, updatedCourse);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Person>> getStudents(@PathVariable int id) {
        return ResponseEntity.ok(courseService.getStudents(id));
    }

    @PostMapping("/{courseId}/{personId}")
    public ResponseEntity<Void> addPersonInCourse(@PathVariable int personId, @PathVariable int courseId) {
        courseService.addPersonInCourse(personId, courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/{personId}")
    public ResponseEntity<Void> removePersonFromCourse(@PathVariable int personId, @PathVariable int courseId) {
        courseService.removePersonFromCourse(personId, courseId);
        return ResponseEntity.noContent().build();
    }
}