package ak.spring.controllers;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.services.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/courses")
@CrossOrigin(origins = {"http://localhost:5173", "https://multiznaika-education.ru"}, allowCredentials = "true")
@Tag(name = "Course Management", description = "Управление учебными курсами")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все курсы")
    @ApiResponse(responseCode = "200", description = "Список курсов успешно получен")
    public List<CourseDTO> findAll() {
        return courseService.findAll();
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить курсы с пагинацией")
    @ApiResponse(responseCode = "200", description = "Данные успешно получены")
    public Page<CourseDTO> findWithPagination(
            @Parameter(description = "Номер страницы") @RequestParam int offset,
            @Parameter(description = "Размер страницы") @RequestParam int pageSize
    ) {
        return courseService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск курсов по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курсы найдены"),
            @ApiResponse(responseCode = "404", description = "Курсы не найдены")
    })
    public List<CourseDTO> findByName(@PathVariable String name) {
        return courseService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый курс")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Курс успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public CourseDTO uploadCourse(@Valid @RequestBody CourseDTO course) {
        return courseService.uploadCourse(course);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить курс по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курс найден"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    public CourseDTO findById(@PathVariable int id) {
        return courseService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить курс")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Курс удалён"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    public void deleteCourse(@PathVariable int id) {
        courseService.deleteCourse(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить данные курса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Курс не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public CourseDTO updateCourse(@PathVariable int id, @Valid @RequestBody CourseDTO updatedCourse) {
        return courseService.updateCourse(id, updatedCourse);
    }

    @GetMapping("/{id}/curriculum")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить учебный план курса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Учебный план найден"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public CurriculumDTO getCurriculumById(@PathVariable int id) {
        return courseService.getCurriculum(id);
    }

    @GetMapping("/{id}/lessons")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить уроки курса")
    @ApiResponse(responseCode = "200", description = "Уроки успешно получены")
    public List<LessonDTO> getLessonsById(@PathVariable int id) {
        return courseService.getLessons(id);
    }

    @PutMapping("/{id}/lessons/{lessonId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить урок к курсу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Урок добавлен"),
            @ApiResponse(responseCode = "404", description = "Курс или урок не найден")
    })
    public CourseDTO addLessonToCourse(@PathVariable int id, @PathVariable int lessonId) {
        return courseService.addLessonToCourse(id, lessonId);
    }

    @GetMapping("/{id}/homeworks")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить домашние задания курса")
    @ApiResponse(responseCode = "200", description = "Домашние задания получены")
    public List<HomeworkDTO> getHomeworksById(@PathVariable int id) {
        return courseService.getHomeworks(id);
    }

    @PutMapping("/{id}/homeworks/{homeworkId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить домашнее задание к курсу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание добавлено"),
            @ApiResponse(responseCode = "404", description = "Курс или задание не найдены")
    })
    public CourseDTO addHomeworkToCourse(@PathVariable int id, @PathVariable int homeworkId) {
        return courseService.addHomeworkToCourse(id, homeworkId);
    }

    @PutMapping("/{courseId}/curriculum/{curriculumId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Привязать учебный план к курсу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "План привязан"),
            @ApiResponse(responseCode = "404", description = "Курс или план не найдены")
    })
    public CourseDTO addCurriculumToCourse(
            @PathVariable int courseId,
            @PathVariable int curriculumId
    ) {
        return courseService.addCurriculumToCourse(courseId, curriculumId);
    }

    @DeleteMapping("/{courseId}/curriculum/{curriculumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отвязать учебный план от курса")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "План отвязан"),
            @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    public void deleteCurriculumFromCourse(
            @PathVariable int courseId,
            @PathVariable int curriculumId
    ) {
        courseService.deleteCurriculumFromCourse(courseId, curriculumId);
    }
}