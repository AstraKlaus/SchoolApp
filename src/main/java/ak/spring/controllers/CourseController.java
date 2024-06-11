package ak.spring.controllers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.models.Course;
import ak.spring.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/courses")
@CrossOrigin(origins =  "http://localhost:5173")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> findAll() {
        List<CourseDTO> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<CourseDTO>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<CourseDTO> courses = courseService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<CourseDTO>> findByName(@PathVariable String name) {
        List<CourseDTO> courses = courseService.findByName(name);
        return ResponseEntity.ok(courses);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> uploadCourse(@Valid @RequestBody Course course) {
        CourseDTO savedCourse = courseService.uploadCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> findById(@PathVariable int id) {
        CourseDTO course = courseService.findById(id);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable int id,
                                                  @Valid @RequestBody CourseDTO updatedCourse) {
        CourseDTO course = courseService.updateCourse(id, updatedCourse);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<CurriculumDTO> getCurriculumById(@PathVariable int id) {
        CurriculumDTO curriculum = courseService.getCurriculum(id);
        return ResponseEntity.ok(curriculum);
    }

    @GetMapping("/{id}/lessons")
    public ResponseEntity<List<LessonDTO>> getLessonsById(@PathVariable int id) {
        List<LessonDTO> lessons = courseService.getLessons(id);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}/homeworks")
    public ResponseEntity<List<HomeworkDTO>> getHomeworksById(@PathVariable int id) {
        List<HomeworkDTO> homeworks = courseService.getHomeworks(id);
        return ResponseEntity.ok(homeworks);
    }
}