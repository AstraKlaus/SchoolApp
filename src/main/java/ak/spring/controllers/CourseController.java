package ak.spring.controllers;

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
        List<Course> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Course>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<Course> courses = courseService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Course>> findByName(@PathVariable String name) {
        List<Course> courses = courseService.findByName(name);
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    public ResponseEntity<Course> uploadCourse(@Valid @RequestBody Course course) {
        Course savedCourse = courseService.uploadCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> findById(@PathVariable int id) {
        Course course = courseService.findById(id);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        Course course = courseService.findById(id);
        courseService.deleteCourse(course);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable int id, @Valid @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(id, updatedCourse);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Person>> getStudents(@PathVariable int id) {
        List<Person> students = courseService.getStudents(id);
        return ResponseEntity.ok(students);
    }
}