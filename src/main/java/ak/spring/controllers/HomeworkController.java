package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.models.Homework;
import ak.spring.services.HomeworkService;
import io.minio.errors.*;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("v1/api/homeworks")
@CrossOrigin(origins = "http://localhost:5173")
public class HomeworkController {

    private final HomeworkService homeworkService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<HomeworkDTO>> findByName(@PathVariable String name) {
        List<HomeworkDTO> homeworks = homeworkService.findByName(name);
        return ResponseEntity.ok(homeworks);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<HomeworkDTO>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<HomeworkDTO> homeworks = homeworkService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(homeworks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HomeworkDTO> findById(@PathVariable int id) {
        HomeworkDTO homework = homeworkService.findById(id);
        return ResponseEntity.ok(homework);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHomework(@PathVariable int id) {
        homeworkService.deleteHomework(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<HomeworkDTO> saveHomework(@Valid @RequestBody Homework homework) {
        HomeworkDTO savedHomework = homeworkService.saveHomework(homework);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHomework);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HomeworkDTO> updateHomework(@PathVariable int id,
                                                      @Valid @RequestBody HomeworkDTO updatedHomework) {
        HomeworkDTO homework = homeworkService.updateHomework(id, updatedHomework);
        return ResponseEntity.ok(homework);
    }

    @GetMapping
    public ResponseEntity<List<HomeworkDTO>> findAll() {
        List<HomeworkDTO> homeworks = homeworkService.findAll();
        return ResponseEntity.ok(homeworks);
    }

    @GetMapping("/{id}/answers")
    public ResponseEntity<List<AnswerDTO>> getAnswers(@PathVariable int id) {
        return ResponseEntity.ok(homeworkService.getAnswers(id));
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<CurriculumDTO> getCurriculum(@PathVariable int id) {
        return ResponseEntity.ok(homeworkService.getCurriculum(id));
    }

    @PutMapping("/{id}/course/{courseId}")
    public ResponseEntity<HomeworkDTO> addCourseToHomework(@PathVariable int id, @PathVariable int courseId) {
        return ResponseEntity.ok(homeworkService.addCourseToHomework(id, courseId));
    }

    @PutMapping("/{id}/answers/{answerId}")
    public ResponseEntity<HomeworkDTO> addAnswerToHomework(@PathVariable int id, @PathVariable int answerId) {
        return ResponseEntity.ok(homeworkService.addAnswerToHomework(id, answerId));
    }


    @DeleteMapping("/{id}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable int id, @PathVariable int answerId) {
        homeworkService.deleteAnswer(id, answerId);
        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    @PostMapping("/{id}/image")
    public ResponseEntity<Void> uploadImage(@PathVariable int id,
                                            @RequestParam("image") MultipartFile image) {
        homeworkService.uploadImage(id, image);
        return ResponseEntity.ok().build();
    }


}

