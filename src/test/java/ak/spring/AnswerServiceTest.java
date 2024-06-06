package ak.spring;
import ak.spring.dto.AnswerDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Answer;
import ak.spring.models.Person;
import ak.spring.repositories.AnswerRepository;
import ak.spring.requests.AnswerRequest;
import ak.spring.services.AnswerService;
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

class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    private Answer answer;
    private Person student;
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        student = Person.builder().id(1).firstName("John").lastName("Doe").build();

        answer = Answer.builder()
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
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer result = answerService.uploadAccord(multipartFile);

        assertEquals(answer, result);
    }

    @Test
    void updateAccord_ShouldUpdateSubmission() {
        when(answerRepository.findById(1)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer result = answerService.updateAccord(1, "Updated file".getBytes());

        assertEquals(answer, result);
        assertEquals("Updated file", new String(result.getFile()));
    }

    @Test
    void findWithPagination_ShouldReturnPageOfSubmissions() {
        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        Page<Answer> page = new PageImpl<>(answers);
        when(answerRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<AnswerDTO> result = answerService.findWithPagination(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(answer.getFeedback(), result.getContent().get(0).getFeedback());
    }

    @Test
    void findById_ShouldReturnSubmission() {
        when(answerRepository.findById(1)).thenReturn(Optional.of(answer));

        Answer result = answerRepository.findById(1).get();

        assertEquals(answer, result);
    }

    @Test
    void findById_ShouldThrowException() {
        when(answerRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> answerService.findById(2));
    }

    @Test
    void downloadAccord_ShouldReturnFileBytes() {
        when(answerRepository.findByFeedbackContainingIgnoreCase("Great work!")).thenReturn(Optional.of(answer));

        byte[] result = answerService.downloadAccord("Great work!");

        assertArrayEquals(answer.getFile(), result);
    }

    @Test
    void findByName_ShouldReturnSubmission() {
        when(answerRepository.findByFeedbackContainingIgnoreCase("Great work!")).thenReturn(Optional.of(answer));

        AnswerDTO result = answerService.findByName("Great work!");

        assertEquals(answer.getFeedback(), result.getFeedback());
    }

    @Test
    void findByName_ShouldThrowException() {
        when(answerRepository.findByFeedbackContainingIgnoreCase("Invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> answerService.findByName("Invalid"));
    }

    @Test
    void save_ShouldSaveSubmission() {
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer result = answerService.save(answer);

        assertEquals(answer, result);
    }

    @Test
    void findAll_ShouldReturnAllSubmissions() {
        List<Answer> answers = new ArrayList<>();
        answers.add(answer);
        when(answerRepository.findAll()).thenReturn(answers);

        List<AnswerDTO> result = answerService.findAll();

        assertEquals(1, result.size());
        assertEquals(answer.getFeedback(), result.get(0).getFeedback());
    }

    @Test
    void delete_ShouldDeleteSubmission() {
        answerService.delete(1);

        verify(answerRepository, times(1)).delete(answer);
    }

    @Test
    void update_ShouldUpdateSubmission() {
        Person updatedStudent = Person.builder().id(2).firstName("Jane").lastName("Doe").build();
        AnswerRequest updatedSubmission = AnswerRequest.builder()
                .feedback("Updated feedback")
                .file("Updated file".getBytes())
                .build();

        when(answerRepository.findById(1)).thenReturn(Optional.of(answer));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer result = answerService.update(1, updatedSubmission);

        assertEquals(updatedStudent, result.getStudent());
        assertEquals("Updated feedback", result.getFeedback());
        assertArrayEquals("Updated file".getBytes(), result.getFile());
    }
}
