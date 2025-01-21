package ak.spring.controllers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.models.Lesson;
import ak.spring.services.LessonService;
import ak.spring.services.MinioService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("v1/api/lessons")
@CrossOrigin(origins =  "http://localhost:5173")
public class LessonController {

    private final LessonService lessonService;
    private final MinioService minioService;

    @Autowired
    public LessonController(LessonService lessonService,
                            MinioService minioService) {
        this.lessonService = lessonService;
        this.minioService = minioService;
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<LessonDTO>> findByName(@PathVariable String name) {
        List<LessonDTO> lessons = lessonService.findByName(name);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<LessonDTO>> findWithPagination(@RequestParam int offset,
                                                              @RequestParam int pageSize) {
        Page<LessonDTO> lessons = lessonService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> findById(@PathVariable int id) {
        LessonDTO lesson = lessonService.findById(id);
        return ResponseEntity.ok(lesson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable int id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<LessonDTO> saveLesson(@Valid @RequestBody LessonDTO lesson) {
        LessonDTO savedLesson = lessonService.saveLesson(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLesson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable int id,
                                               @Valid @RequestBody LessonDTO updatedLesson) {
        LessonDTO lesson = lessonService.updateLesson(id, updatedLesson);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping
    public ResponseEntity<List<LessonDTO>> findAll() {
        List<LessonDTO> lessons = lessonService.findAll();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}/course")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable int id) {
        return ResponseEntity.ok(lessonService.getCourse(id));
    }

    @PutMapping("/{id}/course/{courseId}")
    public ResponseEntity<LessonDTO> addCourseToLesson(@PathVariable int id,
                                                       @PathVariable int courseId) {
        LessonDTO lesson = lessonService.addCourseToLesson(id, courseId);
        return ResponseEntity.ok(lesson);
    }

    @SneakyThrows
    @PostMapping("/{id}/attachments")
    public ResponseEntity<String> uploadImage(@PathVariable int id,
                                            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(minioService.uploadFileToLesson(id, image));
    }

    @DeleteMapping("/{id}/attachments/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable int id, @PathVariable String filename) {
        minioService.removeFileFromLesson(id, filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lessonId}/attachments/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable int lessonId,
                                            @PathVariable String fileName) {
        LessonDTO lesson = lessonService.findById(lessonId);

        return minioService.getResourceResponseEntity(fileName, lesson.getAttachments(), minioService);
    }
}