package ak.spring.controllers;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.models.Answer;
import ak.spring.requests.AnswerRequest;
import ak.spring.services.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("v1/api/answers")
@CrossOrigin(origins =  "http://localhost:5173", allowCredentials = "true")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Answer> uploadAccord(@RequestParam("file") MultipartFile file) throws IOException {
        Answer answer = answerService.uploadAccord(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(answer);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<AnswerDTO> updateAccord(@PathVariable int id, @RequestBody byte[] file) {
        AnswerDTO updatedAnswer = answerService.updateAccord(id, file);
        return ResponseEntity.ok(updatedAnswer);
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

    @GetMapping("/download/{name}")
    public ResponseEntity<byte[]> downloadAccord(@PathVariable String name) {
        byte[] file = answerService.downloadAccord(name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<AnswerDTO> findByName(@PathVariable String name) {
        AnswerDTO answer = answerService.findByName(name);
        return ResponseEntity.ok(answer);
    }

    @PostMapping
    public ResponseEntity<AnswerDTO> save(@Valid @RequestBody Answer answer) {
        AnswerDTO savedAnswer = answerService.save(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnswer);
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
}