package ak.spring.services;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Answer;
import ak.spring.models.Homework;
import ak.spring.models.Person;
import ak.spring.models.Status;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.repositories.StatusRepository;
import ak.spring.services.AnswerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private HomeworkRepository homeworkRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private AnswerDTOMapper answerDTOMapper;

    @Mock
    private PersonDTOMapper personDTOMapper;

    @Mock
    private HomeworkDTOMapper homeworkDTOMapper;

    @InjectMocks
    private AnswerService answerService;

    @Test
    void findById_AnswerExists_ReturnsAnswerDTO() {
        // Arrange
        int answerId = 1;
        Answer answer = new Answer();
        AnswerDTO expectedDTO = new AnswerDTO();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(answerDTOMapper.apply(answer)).thenReturn(expectedDTO);

        // Act
        AnswerDTO result = answerService.findById(answerId);

        // Assert
        assertSame(expectedDTO, result);
        verify(answerRepository).findById(answerId);
    }

    @Test
    void findById_AnswerNotFound_ThrowsException() {
        // Arrange
        int answerId = 1;
        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> answerService.findById(answerId));
    }

    @Test
    void save_ValidAnswer_ReturnsSavedAnswer() {
        // Arrange
        AnswerDTO inputDTO = AnswerDTO.builder()
                .text("Ответ")
                .studentId(1)
                .homeworkId(1)
                .statusId(1)
                .build();

        Person student = new Person();
        Homework homework = new Homework();
        Status status = new Status();
        Answer savedAnswer = new Answer();

        // Настройка моков для зависимостей
        when(personRepository.findById(1)).thenReturn(Optional.of(student));
        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));
        when(answerRepository.save(any())).thenReturn(savedAnswer);

        // Act
        Answer result = answerService.save(inputDTO);

        // Assert
        assertNotNull(result, "Сохраненный ответ не должен быть null");
        verify(answerRepository).save(any()); // Проверка вызова save
    }

    @Test
    void update_ValidData_ReturnsUpdatedAnswerDTO() {
        // Arrange
        int answerId = 1;
        AnswerDTO updatedDTO = AnswerDTO.builder()
                .text("Обновленный ответ")
                .studentId(2)
                .homeworkId(2)
                .statusId(2)
                .build();

        Answer existingAnswer = new Answer();
        Person newStudent = new Person();
        Homework newHomework = new Homework();
        Status newStatus = new Status();
        AnswerDTO expectedDTO = new AnswerDTO();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(existingAnswer));
        when(personRepository.findById(2)).thenReturn(Optional.of(newStudent));
        when(homeworkRepository.findById(2)).thenReturn(Optional.of(newHomework));
        when(statusRepository.findById(2)).thenReturn(Optional.of(newStatus));
        when(answerDTOMapper.apply(existingAnswer)).thenReturn(expectedDTO);

        // Act
        AnswerDTO result = answerService.update(answerId, updatedDTO);

        // Assert
        assertSame(expectedDTO, result);
        assertEquals("Обновленный ответ", existingAnswer.getText());
        verify(answerRepository).save(existingAnswer);
    }

    @Test
    void delete_AnswerExists_DeletesAnswer() {
        // Arrange
        int answerId = 1;
        Answer answer = new Answer();
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));

        // Act
        answerService.delete(answerId);

        // Assert
        verify(answerRepository).delete(answer);
    }

    @Test
    void getStudent_AnswerExists_ReturnsStudentDTO() {
        // Arrange
        int answerId = 1;
        Answer answer = mock(Answer.class);
        Person student = new Person();
        PersonDTO expectedDTO = new PersonDTO();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(answer.getStudent()).thenReturn(student);
        when(personDTOMapper.apply(student)).thenReturn(expectedDTO);

        // Act
        PersonDTO result = answerService.getStudent(answerId);

        // Assert
        assertSame(expectedDTO, result);
        verify(answerRepository).findById(answerId);
    }

    @Test
    void addPersonToAnswer_ValidIds_UpdatesAnswer() {
        // Arrange
        int answerId = 1;
        int personId = 2;
        Answer answer = new Answer();
        Person person = new Person();
        AnswerDTO expectedDTO = new AnswerDTO();

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(personRepository.findById(personId)).thenReturn(Optional.of(person));
        when(answerDTOMapper.apply(answer)).thenReturn(expectedDTO);

        // Act
        AnswerDTO result = answerService.addPersonToAnswer(answerId, personId);

        // Assert
        assertSame(expectedDTO, result);
        assertEquals(person, answer.getStudent());
        verify(answerRepository).save(answer);
    }
}