package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Classroom;
import ak.spring.services.ClassroomService;
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
@RequestMapping("v1/api/classrooms")
@CrossOrigin(origins = {"http://localhost:5173", "https://multiznaika-education.ru"}, allowCredentials = "true")
@Tag(name = "Classroom Management", description = "Управление учебными классами")
public class ClassroomController {

    private final ClassroomService classroomService;

    @Autowired
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск классов по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Классы найдены"),
            @ApiResponse(responseCode = "404", description = "Классы не найдены")
    })
    public List<ClassroomDTO> findByName(@PathVariable String name) {
        return classroomService.findByName(name);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить классы с пагинацией")
    @ApiResponse(responseCode = "200", description = "Успешное получение данных")
    public Page<ClassroomDTO> findWithPagination(
            @Parameter(description = "Номер страницы") @RequestParam int offset,
            @Parameter(description = "Размер страницы") @RequestParam int pageSize
    ) {
        return classroomService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить класс по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Класс найден"),
            @ApiResponse(responseCode = "404", description = "Класс не найден")
    })
    public ClassroomDTO findById(@PathVariable int id) {
        return classroomService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить данные класса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Класс не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ClassroomDTO updateGroup(@PathVariable int id, @Valid @RequestBody ClassroomDTO updatedClassroom) {
        return classroomService.updateGroup(id, updatedClassroom);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить класс")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Класс удалён"),
            @ApiResponse(responseCode = "404", description = "Класс не найден")
    })
    public void deleteGroup(@PathVariable int id) {
        classroomService.deleteGroup(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый класс")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Класс создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public ClassroomDTO uploadGroup(@Valid @RequestBody Classroom classroom) {
        return classroomService.uploadGroup(classroom);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все классы")
    @ApiResponse(responseCode = "200", description = "Список классов получен")
    public List<ClassroomDTO> findAll() {
        return classroomService.findAll();
    }

    @GetMapping("/{id}/students")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить студентов класса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студенты найдены"),
            @ApiResponse(responseCode = "404", description = "Класс не найден")
    })
    public List<PersonDTO> getStudents(@PathVariable int id) {
        return classroomService.getStudents(id);
    }

    @PutMapping("/{classroomId}/students/{studentId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить студента в класс")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Студент добавлен"),
            @ApiResponse(responseCode = "404", description = "Класс или студент не найден")
    })
    public ClassroomDTO addStudentToClassroom(
            @PathVariable int classroomId,
            @PathVariable int studentId
    ) {
        return classroomService.addStudentToClassroom(classroomId, studentId);
    }

    @DeleteMapping("/{classroomId}/students/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить студента из класса")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Студент удалён"),
            @ApiResponse(responseCode = "404", description = "Класс или студент не найден")
    })
    public void deleteStudentFromClassroom(
            @PathVariable int classroomId,
            @PathVariable int studentId
    ) {
        classroomService.deleteStudentFromClassroom(classroomId, studentId);
    }

    @PutMapping("/{classroomId}/curriculum/{curriculumId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить учебный план к классу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Учебный план добавлен"),
            @ApiResponse(responseCode = "404", description = "Класс или учебный план не найден")
    })
    public ClassroomDTO addClassroomToCurriculum(
            @PathVariable int classroomId,
            @PathVariable int curriculumId
    ) {
        return classroomService.addClassroomToCurriculum(classroomId, curriculumId);
    }

    @DeleteMapping("/{classroomId}/curriculum/{curriculumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить учебный план у класса")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Учебный план удалён"),
            @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    public void deleteClassroomFromCurriculum(
            @PathVariable int classroomId,
            @PathVariable int curriculumId
    ) {
        classroomService.deleteClassroomFromCurriculum(classroomId, curriculumId);
    }

    @GetMapping("/{id}/curriculum")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить учебный план класса")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Учебный план найден"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public List<CurriculumDTO> getCurriculumById(@PathVariable int id) {
        return classroomService.getCurricula(id);
    }
}