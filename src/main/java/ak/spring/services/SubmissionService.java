package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Submission;
import ak.spring.repositories.SubmissionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission uploadAccord(MultipartFile file) throws IOException {
        return submissionRepository.save(Submission.builder()
                .file(file.getBytes())
                .build());
    }

    public Submission updateAccord(int id, byte[] file) {
        Submission submission = findById(id);
        submission.setFile(file);
        return submissionRepository.save(submission);
    }

    public Page<Submission> findWithPagination(int offset, int pageSize) {
        return submissionRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Submission findById(int id) {
        return submissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
    }

    public byte[] downloadAccord(String name) {
        return findByName(name).getFile();}

    public Submission findByName(String name) {
        return submissionRepository.findByNameContainingIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "name", name));
    }

    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }

    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }

    public void delete(Submission submission) {
        submissionRepository.delete(submission);
    }

    public Submission update(int id, Submission updatedSubmission) {
        Submission existingSubmission = findById(id);
        existingSubmission.setStudent(updatedSubmission.getStudent());
        existingSubmission.setFile(updatedSubmission.getFile());
        existingSubmission.setFeedback(updatedSubmission.getFeedback());
        return submissionRepository.save(existingSubmission);
    }
}
