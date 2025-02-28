package ak.spring.controllers;

import ak.spring.dto.EnrollmentDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Enrollment;
import ak.spring.models.Lesson;
import ak.spring.services.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "http://localhost", allowCredentials = "true")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> findAll() {
        List<EnrollmentDTO> enrollments = enrollmentService.findAll();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDTO> findById(@PathVariable int id) {
        EnrollmentDTO enrollment = enrollmentService.findById(id);
        return ResponseEntity.ok(enrollment);
    }

    @PostMapping
    public ResponseEntity<Enrollment> save(@Valid @RequestBody Enrollment enrollment) {
        Enrollment savedEnrollment = enrollmentService.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnrollment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        enrollmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enrollment> update(@PathVariable int id, @Valid @RequestBody Enrollment updatedEnrollment) {
        Enrollment enrollment = enrollmentService.update(id, updatedEnrollment);
        return ResponseEntity.ok(enrollment);
    }
}