package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.models.Curriculum;
import ak.spring.services.CurriculumService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("v1/api/curricula")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Curriculum Management", description = "Управление учебными планами")
public class CurriculumController {

    private final CurriculumService curriculumService;

    @Autowired
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все учебные планы")
    @ApiResponse(responseCode = "200", description = "Список учебных планов получен")
    public List<CurriculumDTO> getAllCurricula() {
        return curriculumService.getAllCurricula();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить учебный план по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Учебный план найден"),
            @ApiResponse(responseCode = "404", description = "Учебный план не найден")
    })
    public CurriculumDTO getCurriculumById(@PathVariable int id) {
        return curriculumService.getCurriculumById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый учебный план")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Учебный план создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public CurriculumDTO createCurriculum(@Valid @RequestBody Curriculum curriculum) {
        return curriculumService.saveCurriculum(curriculum);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить учебный план")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Учебный план не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public CurriculumDTO updateCurriculum(@PathVariable int id,
                                          @Valid @RequestBody CurriculumDTO curriculumDetails) {
        return curriculumService.updateCurriculum(id, curriculumDetails);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить учебный план")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Учебный план удалён"),
            @ApiResponse(responseCode = "404", description = "Учебный план не найден")
    })
    public void deleteCurriculum(@PathVariable int id) {
        curriculumService.deleteCurriculum(id);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список учебных планов с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список учебных планов получен"),
            @ApiResponse(responseCode = "404", description = "Список учебных планов не получен")
    })
    public Page<CurriculumDTO> getAllCurriculaWithPagination(@RequestParam() int page, @RequestParam() int size) {
        return curriculumService.findWithPagination(page, size);
    }

    @GetMapping("/{id}/courses")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить курсы учебного плана")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Курсы найдены"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public List<CourseDTO> getCourseById(@PathVariable int id) {
        return curriculumService.getCourseById(id);
    }

    @GetMapping("/{curriculumId}/classrooms")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить классы учебного плана")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Классы найдены"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public List<ClassroomDTO> getClassroomById(@PathVariable int curriculumId) {
        return curriculumService.getClassroomById(curriculumId);
    }

    @GetMapping("/{curriculumId}/classrooms/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список классов учебного плана с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Классы найдены"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public Page<ClassroomDTO> getPaginatedClassrooms(
            @PathVariable int curriculumId,
            @RequestParam() int page,
            @RequestParam() int size) {

        return curriculumService.getPaginatedClassrooms(curriculumId, page, size);
    }



    @PutMapping("/{curriculumId}/classrooms/{classroomId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Добавить класс к учебному плану")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Класс добавлен"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public CurriculumDTO addClassroomToCurriculum(@PathVariable int curriculumId,
                                                  @PathVariable int classroomId) {
        return curriculumService.addClassroomToCurriculum(curriculumId, classroomId);
    }

    @DeleteMapping("/{curriculumId}/classrooms/{classroomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить класс из учебного плана")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Класс удалён"),
            @ApiResponse(responseCode = "404", description = "Связь не найдена")
    })
    public void deleteClassroomFromCurriculum(@PathVariable int curriculumId,
                                              @PathVariable int classroomId) {
        curriculumService.deleteClassroomFromCurriculum(curriculumId, classroomId);
    }

    @GetMapping("/search/{name}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск учебных планов по названию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Планы найдены"),
            @ApiResponse(responseCode = "404", description = "Планы не найдены")
    })
    public List<CurriculumDTO> findByName(@PathVariable String name) {
        return curriculumService.findByName(name);
    }
}