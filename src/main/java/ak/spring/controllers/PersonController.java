package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.dto.SettingsDTO;
import ak.spring.models.Person;
import ak.spring.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("v1/api/people")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Person Management", description = "Управление пользователями системы")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей")
    public List<PersonDTO> findAll() {
        return personService.findAll();
    }

    @GetMapping("/search/{username}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Поиск пользователя по имени")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public PersonDTO findByUsername(@PathVariable String username) {
        return personService.findByUsername(username);
    }

    @GetMapping("/token/{token}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Найти пользователя по токену")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public Person findByToken(@PathVariable String token) {
        return personService.findByToken(token);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public PersonDTO findById(@PathVariable int id) {
        return personService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать нового пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя")
    })
    public PersonDTO uploadPerson(@Valid @RequestBody Person person) {
        return personService.uploadPerson(person);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public void deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить данные пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public PersonDTO update(@PathVariable int id, @Valid @RequestBody PersonDTO updatedPerson) {
        return personService.update(id, updatedPerson);
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить список пользователей с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "404", description = "Список пользователей не получен")
    })
    public Page<PersonDTO> getAllWithPagination(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size)
    { return personService.findWithPagination(page, size); }

    @GetMapping("/{personId}/classroom")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить класс пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Класс найден"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public ClassroomDTO findClassroomForPerson(@PathVariable int personId) {
        return personService.findClassroomForPerson(personId);
    }

    @PutMapping("/{personId}/classroom/{classroomId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить класс пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Класс обновлён"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public PersonDTO updateClassroomForPerson(@PathVariable int personId, @PathVariable int classroomId) {
        return personService.updateClassroomForPerson(personId, classroomId);
    }

    @GetMapping("/{personId}/settings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить настройки пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Настройки найдены"),
            @ApiResponse(responseCode = "404", description = "Настройки не найдены")
    })
    public SettingsDTO findSettingsForPerson(@PathVariable int personId) {
        return personService.findSettingsForPerson(personId);
    }

    @GetMapping("/{personId}/answer")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить ответы пользователя")
    @ApiResponse(responseCode = "200", description = "Ответы успешно получены")
    public List<AnswerDTO> findAnswerForPerson(@PathVariable int personId) {
        return personService.findAnswerForPerson(personId);
    }

    @DeleteMapping("/{personId}/settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить настройки пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Настройки удалены"),
            @ApiResponse(responseCode = "404", description = "Настройки не найдены")
    })
    public void deleteSettingsForPerson(@PathVariable int personId) {
        personService.deleteSettingsForPerson(personId);
    }

    @DeleteMapping("/{personId}/classroom")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить класс у пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Класс удалён"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public void deleteClassroomForPerson(@PathVariable int personId) {
        personService.deleteClassroomForPerson(personId);
    }
}