package ak.spring.services;

import ak.spring.dto.SubmissionDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.SubmissionDTOMapper;
import ak.spring.models.Submission;
import ak.spring.repositories.SubmissionRepository;
import ak.spring.requests.SubmissionRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionDTOMapper submissionDTOMapper;

    @Autowired
    public SubmissionService(SubmissionRepository submissionRepository,
                             SubmissionDTOMapper submissionDTOMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionDTOMapper = submissionDTOMapper;
    }

    public Submission uploadAccord(MultipartFile file) throws IOException {
        return submissionRepository.save(Submission.builder()
                .file(file.getBytes())
                .build());
    }

    public Submission updateAccord(int id, byte[] file) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
        submission.setFile(file);
        return submissionRepository.save(submission);
    }

    public Page<SubmissionDTO> findWithPagination(int offset, int pageSize) {
        return submissionRepository.findAll(PageRequest.of(offset, pageSize)).map(submissionDTOMapper);
    }

    public SubmissionDTO findById(int id) {
        return submissionRepository.findById(id).map(submissionDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
    }

    public byte[] downloadAccord(String name) {
        return findByName(name).getFile();
    }

    public SubmissionDTO findByName(String name) {
        return submissionRepository.findByFeedbackContainingIgnoreCase(name).map(submissionDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "name", name));
    }

    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }

    public List<SubmissionDTO> findAll() {
        return submissionRepository.findAll()
                .stream()
                .map(submissionDTOMapper)
                .toList();
    }

    public void delete(int id) {
        Submission existingSubmission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
        submissionRepository.delete(existingSubmission);
    }
    public Submission update(int id, SubmissionRequest updatedSubmission) {
        Submission existingSubmission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));
        existingSubmission.setFile(updatedSubmission.getFile());
        existingSubmission.setFeedback(updatedSubmission.getFeedback());
        existingSubmission.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return submissionRepository.save(existingSubmission);
    }
}
