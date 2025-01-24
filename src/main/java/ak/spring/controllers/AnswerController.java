package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Answer;
import ak.spring.services.AnswerService;
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
@RequestMapping("v1/api/answers")
@CrossOrigin(origins =  "http://localhost:5173", allowCredentials = "true")
public class AnswerController {

    private final AnswerService answerService;

    private final MinioService minioService;

    @Autowired
    public AnswerController(AnswerService answerService,
                            MinioService minioService) {
        this.answerService = answerService;
        this.minioService = minioService;
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<AnswerDTO>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<AnswerDTO> submissions = answerService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnswerDTO> findById(@PathVariable int id) {
        AnswerDTO submission = answerService.findById(id);
        return ResponseEntity.ok(submission);
    }

    @PostMapping
    public ResponseEntity<AnswerDTO> save(@Valid @RequestBody AnswerDTO answer) {
        Answer savedAnswer = answerService.save(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(answerService.findById(savedAnswer.getId()));
    }

    @GetMapping
    public ResponseEntity<List<AnswerDTO>> findAll() {
        List<AnswerDTO> answers = answerService.findAll();
        return ResponseEntity.ok(answers);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        answerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerDTO> update(@PathVariable int id, @Valid @RequestBody AnswerDTO updatedAnswer) {
        AnswerDTO answer = answerService.update(id, updatedAnswer);
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/{id}/person")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable int id) {
        PersonDTO person = answerService.getStudent(id);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{id}/homework")
    public ResponseEntity<HomeworkDTO> getHomework(@PathVariable int id) {
        HomeworkDTO homework = answerService.getHomework(id);
        return ResponseEntity.ok(homework);
    }

    @PutMapping("/{id}/person/{personId}")
    public ResponseEntity<AnswerDTO> addPersonToAnswer(@PathVariable int id, @PathVariable int personId) {
        return ResponseEntity.ok(answerService.addPersonToAnswer(id, personId));
    }

    @SneakyThrows
    @PostMapping("/{id}/attachments")
    public ResponseEntity<String> uploadImage(@PathVariable int id,
                                            @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(minioService.uploadFileToAnswer(id, image));
    }

    @DeleteMapping("/{id}/attachments/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable int id, @PathVariable String fileName) {
        minioService.removeFileFromAnswer(id, fileName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{answerId}/attachments/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable int answerId,
                                            @PathVariable String fileName) {
        AnswerDTO answer = answerService.findById(answerId);

        return  minioService.getResourceResponseEntity(fileName, answer.getAttachments());
    }

}