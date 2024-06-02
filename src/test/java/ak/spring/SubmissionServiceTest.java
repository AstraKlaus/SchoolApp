package ak.spring;
import ak.spring.dto.SubmissionDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Person;
import ak.spring.models.Submission;
import ak.spring.repositories.SubmissionRepository;
import ak.spring.services.SubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private SubmissionService submissionService;

    private Submission submission;
    private Person student;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        student = Person.builder().id(1).firstName("John").lastName("Doe").build();

        submission = Submission.builder()
                .id(1)
                .feedback("Great work!")
                .file("Hello, World!".getBytes())
                .student(student)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
    }

    @Test
    void uploadAccord_ShouldUploadSubmission() throws IOException {
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission result = submissionService.uploadAccord(multipartFile);

        assertEquals(submission, result);
    }

    @Test
    void updateAccord_ShouldUpdateSubmission() {
        when(submissionRepository.findById(1)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission result = submissionService.updateAccord(1, "Updated file".getBytes());

        assertEquals(submission, result);
        assertEquals("Updated file", new String(result.getFile()));
    }

    @Test
    void findWithPagination_ShouldReturnPageOfSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        submissions.add(submission);
        Page<Submission> page = new PageImpl<>(submissions);
        when(submissionRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<SubmissionDTO> result = submissionService.findWithPagination(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(submission.getFeedback(), result.getContent().get(0).getFeedback());
    }

    @Test
    void findById_ShouldReturnSubmission() {
        when(submissionRepository.findById(1)).thenReturn(Optional.of(submission));

        Submission result = submissionRepository.findById(1).get();

        assertEquals(submission, result);
    }

    @Test
    void findById_ShouldThrowException() {
        when(submissionRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> submissionService.findById(2));
    }

    @Test
    void downloadAccord_ShouldReturnFileBytes() {
        when(submissionRepository.findByFeedbackContainingIgnoreCase("Great work!")).thenReturn(Optional.of(submission));

        byte[] result = submissionService.downloadAccord("Great work!");

        assertArrayEquals(submission.getFile(), result);
    }

    @Test
    void findByName_ShouldReturnSubmission() {
        when(submissionRepository.findByFeedbackContainingIgnoreCase("Great work!")).thenReturn(Optional.of(submission));

        SubmissionDTO result = submissionService.findByName("Great work!");

        assertEquals(submission.getFeedback(), result.getFeedback());
    }

    @Test
    void findByName_ShouldThrowException() {
        when(submissionRepository.findByFeedbackContainingIgnoreCase("Invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> submissionService.findByName("Invalid"));
    }

    @Test
    void save_ShouldSaveSubmission() {
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission result = submissionService.save(submission);

        assertEquals(submission, result);
    }

    @Test
    void findAll_ShouldReturnAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        submissions.add(submission);
        when(submissionRepository.findAll()).thenReturn(submissions);

        List<SubmissionDTO> result = submissionService.findAll();

        assertEquals(1, result.size());
        assertEquals(submission.getFeedback(), result.get(0).getFeedback());
    }

    @Test
    void delete_ShouldDeleteSubmission() {
        submissionService.delete(1);

        verify(submissionRepository, times(1)).delete(submission);
    }

    @Test
    void update_ShouldUpdateSubmission() {
        Person updatedStudent = Person.builder().id(2).firstName("Jane").lastName("Doe").build();
        Submission updatedSubmission = Submission.builder()
                .feedback("Updated feedback")
                .file("Updated file".getBytes())
                .student(updatedStudent)
                .build();

        when(submissionRepository.findById(1)).thenReturn(Optional.of(submission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission result = submissionService.update(1, updatedSubmission);

        assertEquals(updatedStudent, result.getStudent());
        assertEquals("Updated feedback", result.getFeedback());
        assertArrayEquals("Updated file".getBytes(), result.getFile());
    }
}
