package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.services.AnswerService;
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
@RequestMapping("v1/api/answers")
@CrossOrigin(origins = {"http://localhost:5173", "https://multiznaika-education.ru"}, allowCredentials = "true")
@Tag(name = "Answer Management", description = "Управление ответами на задания")
public class AnswerController {

    private final AnswerService answerService;
    private final MinioService minioService;
    private final AnswerDTOMapper answerDTOMapper;

    @Autowired
    public AnswerController(AnswerService answerService,
                            MinioService minioService, AnswerDTOMapper answerDTOMapper) {
        this.answerService = answerService;
        this.minioService = minioService;
        this.answerDTOMapper = answerDTOMapper;
    }

    @GetMapping("/paginated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить ответы с пагинацией")
    @ApiResponse(responseCode = "200", description = "Данные успешно получены")
    public Page<AnswerDTO> findWithPagination(
            @Parameter(description = "Номер страницы") @RequestParam int offset,
            @Parameter(description = "Размер страницы") @RequestParam int pageSize
    ) {
        return answerService.findWithPagination(offset, pageSize);
    }

    @GetMapping("/homework/{homeworkId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить ответы для домашнего задания с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответы успешно получены"),
            @ApiResponse(responseCode = "404", description = "Домашнее задание не найдено")
    })
    public Page<AnswerDTO> findAnswersByHomeworkId(
            @PathVariable int homeworkId,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int pageSize
    ) {
        return answerService.findAnswersByHomeworkId(homeworkId, offset, pageSize);
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить ответ по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ответ найден"),
            @ApiResponse(responseCode = "404", description = "Ответ не найден")
    })
    public AnswerDTO findById(@PathVariable int id) {
        return answerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый ответ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ответ создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public AnswerDTO save(@Valid @RequestBody AnswerDTO answer) {
        return answerDTOMapper.apply(answerService.save(answer));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить все ответы")
    @ApiResponse(responseCode = "200", description = "Список ответов получен")
    public List<AnswerDTO> findAll() {
        return answerService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить ответ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ответ удалён"),
            @ApiResponse(responseCode = "404", description = "Ответ не найден")
    })
    public void delete(@PathVariable int id) {
        answerService.delete(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить ответ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные обновлены"),
            @ApiResponse(responseCode = "404", description = "Ответ не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public AnswerDTO update(@PathVariable int id, @Valid @RequestBody AnswerDTO updatedAnswer) {
        return answerService.update(id, updatedAnswer);
    }

    @GetMapping("/{id}/person")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить автора ответа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public PersonDTO getPerson(@PathVariable int id) {
        return answerService.getStudent(id);
    }

    @GetMapping("/{id}/homework")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить задание ответа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задание найдено"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public HomeworkDTO getHomework(@PathVariable int id) {
        return answerService.getHomework(id);
    }

    @PutMapping("/{id}/person/{personId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Привязать пользователя к ответу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь привязан"),
            @ApiResponse(responseCode = "404", description = "Данные не найдены")
    })
    public AnswerDTO addPersonToAnswer(@PathVariable int id, @PathVariable int personId) {
        return answerService.addPersonToAnswer(id, personId);
    }

    @SneakyThrows
    @PostMapping("/{id}/attachments")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Загрузить вложение ответа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл загружен"),
            @ApiResponse(responseCode = "404", description = "Ответ не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка загрузки")
    })
    public String uploadImage(
            @PathVariable int id,
            @Parameter(description = "Файл для загрузки") @RequestParam("image") MultipartFile image
    ) {
        return minioService.uploadFileToAnswer(id, image);
    }

    @DeleteMapping("/{id}/attachments/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить вложение ответа")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Файл удалён"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    public void deleteImage(@PathVariable int id, @PathVariable String fileName) {
        minioService.removeFileFromAnswer(id, fileName);
    }

    @GetMapping("/{answerId}/attachments/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Скачать вложение ответа")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл получен"),
            @ApiResponse(responseCode = "404", description = "Файл не найден")
    })
    public ResponseEntity<Resource> getFile(
            @PathVariable int answerId,
            @PathVariable String fileName
    ) {
        return minioService.getResourceResponseEntity(fileName, answerService.findById(answerId).getAttachments());
    }
}