package ak.spring.controllers;

import ak.spring.dto.SubmissionDTO;
import ak.spring.models.Submission;
import ak.spring.requests.SubmissionRequest;
import ak.spring.services.SubmissionService;
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
@RequestMapping("/api/submissions")
@CrossOrigin(origins =  "http://localhost:8080")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Submission> uploadAccord(@RequestParam("file") MultipartFile file) throws IOException {
        Submission submission = submissionService.uploadAccord(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Submission> updateAccord(@PathVariable int id, @RequestBody byte[] file) {
        Submission updatedSubmission = submissionService.updateAccord(id, file);
        return ResponseEntity.ok(updatedSubmission);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<SubmissionDTO>> findWithPagination(@RequestParam int offset, @RequestParam int pageSize) {
        Page<SubmissionDTO> submissions = submissionService.findWithPagination(offset, pageSize);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDTO> findById(@PathVariable int id) {
        SubmissionDTO submission = submissionService.findById(id);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<byte[]> downloadAccord(@PathVariable String name) {
        byte[] file = submissionService.downloadAccord(name);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<SubmissionDTO> findByName(@PathVariable String name) {
        SubmissionDTO submission = submissionService.findByName(name);
        return ResponseEntity.ok(submission);
    }

    @PostMapping
    public ResponseEntity<Submission> save(@Valid @RequestBody Submission submission) {
        Submission savedSubmission = submissionService.save(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSubmission);
    }

    @GetMapping
    public ResponseEntity<List<SubmissionDTO>> findAll() {
        List<SubmissionDTO> submissions = submissionService.findAll();
        return ResponseEntity.ok(submissions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        submissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Submission> update(@PathVariable int id, @Valid @RequestBody SubmissionRequest updatedSubmission) {
        Submission submission = submissionService.update(id, updatedSubmission);
        return ResponseEntity.ok(submission);
    }
}