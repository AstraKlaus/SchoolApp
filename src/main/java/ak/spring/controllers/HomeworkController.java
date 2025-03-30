package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.services.HomeworkService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("v1/api/homeworks")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('TEACHER') or hasAuthority('STUDENT')")
@CrossOrigin(origins = {"http://localhost:5173", "https://multiznaika-education.ru"}, allowCredentials = "true")
@Tag(name = "Homework Management", description = "Управление домашними заданиями")
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final MinioService minioService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService,
                              MinioService minioService) {
        this.homeworkService = homeworkService;
        this.minioService = minioService;
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск заданий по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задания найдены"),
            @ApiResponse(responseCode = "404", description = "Задания не найдены")
    })
    public List<HomeworkDTO> findByName(@PathVariable String name) {
        return homeworkService.findByName(name);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить задания с пагинацией")
    @ApiResponse(responseCode = "200", description = "Данные успешно получены")
    public Page<HomeworkDTO> findWithPagination(
            @Parameter(description = "Номер страницы") @RequestParam int offset,
            @Parameter(description = "Размер страницы") @RequestParam int pageSize
    ) {
        return homeworkService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить задание по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание найдено"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено")
    })
    public HomeworkDTO findById(@PathVariable int id) {
        return homeworkService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить задание")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задание удалено"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено")
    })
    public void deleteHomework(@PathVariable int id) {
        homeworkService.deleteHomework(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новое задание")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задание создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public HomeworkDTO saveHomework(@Valid @RequestBody HomeworkDTO homework) {
        return homeworkService.saveHomework(homework);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить данные задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public HomeworkDTO updateHomework(@PathVariable int id,
                                      @Valid @RequestBody HomeworkDTO updatedHomework) {
        return homeworkService.updateHomework(id, updatedHomework);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все задания")
    @ApiResponse(responseCode = "200", description = "Список заданий получен")
    public List<HomeworkDTO> findAll() {
        return homeworkService.findAll();
    }

    @GetMapping("/{id}/answers")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить ответы на задание")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответы найдены"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public List<AnswerDTO> getAnswers(@PathVariable int id) {
        return homeworkService.getAnswers(id);
    }

    @GetMapping("/{id}/curriculum")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить учебный план задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "План найден"),
            @ApiResponse(responseCode = "404", description = "План не найден")
    })
    public CurriculumDTO getCurriculum(@PathVariable int id) {
        return homeworkService.getCurriculum(id);
    }

    @PutMapping("/{id}/course/{courseId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Привязать задание к курсу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание привязано"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public HomeworkDTO addCourseToHomework(@PathVariable int id, @PathVariable int courseId) {
        return homeworkService.addCourseToHomework(id, courseId);
    }

    @PutMapping("/{id}/answers/{answerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить ответ к заданию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответ добавлен"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public HomeworkDTO addAnswerToHomework(@PathVariable int id, @PathVariable int answerId) {
        return homeworkService.addAnswerToHomework(id, answerId);
    }

    @DeleteMapping("/{id}/answers/{answerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить ответ из задания")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ответ удалён"),
            @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    public void deleteAnswer(@PathVariable int id, @PathVariable int answerId) {
        homeworkService.deleteAnswer(id, answerId);
    }

    @SneakyThrows
    @PostMapping("/{id}/attachments")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Загрузить вложение задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл загружен"),
            @ApiResponse(responseCode = "404", description = "Задание не найдено"),
            @ApiResponse(responseCode = "400", description = "Ошибка загрузки")
    })
    public String uploadImage(
            @PathVariable int id,
            @Parameter(description = "Файл для загрузки") @RequestParam("image") MultipartFile image
    ) {
        return minioService.uploadFileToHomework(id, image);
    }

    @DeleteMapping("/{id}/attachments/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить вложение задания")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Файл удалён"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    public void deleteImage(@PathVariable int id, @PathVariable String filename) {
        minioService.removeFileFromHomework(id, filename);
    }

    @GetMapping("/{homeworkId}/attachments/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Скачать вложение задания")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл получен"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    public ResponseEntity<Resource> getFile(
            @PathVariable int homeworkId,
            @PathVariable String fileName
    ) {
        return minioService.getResourceResponseEntity(fileName, homeworkService.findById(homeworkId).getAttachments());
    }
}