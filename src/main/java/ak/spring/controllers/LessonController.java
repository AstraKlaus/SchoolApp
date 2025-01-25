package ak.spring.controllers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.services.LessonService;
import ak.spring.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Lesson Management", description = "Управление уроками")
public class LessonController {

    private final LessonService lessonService;
    private final MinioService minioService;

    @Autowired
    public LessonController(LessonService lessonService, MinioService minioService) {
        this.lessonService = lessonService;
        this.minioService = minioService;
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск уроков по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Уроки найдены"),
            @ApiResponse(responseCode = "404", description = "Уроки не найдены")
    })
    public List<LessonDTO> findByName(@PathVariable String name) {
        return lessonService.findByName(name);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить уроки с пагинацией")
    @ApiResponse(responseCode = "200", description = "Данные успешно получены")
    public Page<LessonDTO> findWithPagination(
            @Parameter(description = "Номер страницы") @RequestParam int offset,
            @Parameter(description = "Размер страницы") @RequestParam int pageSize
    ) {
        return lessonService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить урок по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Урок найден"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    public LessonDTO findById(@PathVariable int id) {
        return lessonService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить урок")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Урок удалён"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    public void deleteLesson(@PathVariable int id) {
        lessonService.deleteLesson(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый урок")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Урок создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public LessonDTO saveLesson(@Valid @RequestBody LessonDTO lesson) {
        return lessonService.saveLesson(lesson);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить данные урока")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Урок не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public LessonDTO updateLesson(@PathVariable int id, @Valid @RequestBody LessonDTO updatedLesson) {
        return lessonService.updateLesson(id, updatedLesson);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все уроки")
    @ApiResponse(responseCode = "200", description = "Список уроков получен")
    public List<LessonDTO> findAll() {
        return lessonService.findAll();
    }

    @GetMapping("/{id}/course")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить курс урока")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курс найден"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    public CourseDTO getCourse(@PathVariable int id) {
        return lessonService.getCourse(id);
    }

    @PutMapping("/{id}/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Привязать урок к курсу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Урок привязан"),
            @ApiResponse(responseCode = "404", description = "Урок или курс не найден")
    })
    public LessonDTO addCourseToLesson(@PathVariable int id, @PathVariable int courseId) {
        return lessonService.addCourseToLesson(id, courseId);
    }

    @SneakyThrows
    @PostMapping("/{id}/attachments")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Загрузить вложение к уроку")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл загружен"),
            @ApiResponse(responseCode = "404", description = "Урок не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка загрузки файла")
    })
    public String uploadImage(
            @PathVariable int id,
            @Parameter(description = "Файл для загрузки") @RequestParam("image") MultipartFile image
    ) {
        return minioService.uploadFileToLesson(id, image);
    }

    @DeleteMapping("/{id}/attachments/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить вложение урока")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Файл удалён"),
            @ApiResponse(responseCode = "404", description = "Файл или урок не найден")
    })
    public void deleteFile(@PathVariable int id, @PathVariable String filename) {
        minioService.removeFileFromLesson(id, filename);
    }

    @GetMapping("/{lessonId}/attachments/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Скачать вложение урока")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл получен"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    public ResponseEntity<Resource> getFile(
            @PathVariable int lessonId,
            @PathVariable String fileName
    ) {
        return minioService.getResourceResponseEntity(fileName, lessonService.findById(lessonId).getAttachments());
    }
}