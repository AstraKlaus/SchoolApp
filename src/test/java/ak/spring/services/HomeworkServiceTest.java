package ak.spring.services;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.models.*;
import ak.spring.repositories.AnswerRepository;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.HomeworkRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeworkServiceTest {

    @Mock
    private HomeworkRepository homeworkRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private CurriculumDTOMapper curriculumDTOMapper;

    @Mock
    private AnswerDTOMapper answerDTOMapper;

    @Mock
    private HomeworkDTOMapper homeworkDTOMapper;

    @InjectMocks
    private HomeworkService homeworkService;

    @Test
    void findById_ExistingId_ReturnsHomeworkDTO() {
        // Arrange
        Homework homework = Homework.builder().id(1).build();
        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));
        when(homeworkDTOMapper.apply(homework)).thenReturn(new HomeworkDTO());

        // Act
        HomeworkDTO result = homeworkService.findById(1);

        // Assert
        assertNotNull(result);
        verify(homeworkRepository).findById(1);
    }

    @Test
    void findById_NonExistingId_ThrowsException() {
        // Arrange
        when(homeworkRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.findById(999));
    }

    @Test
    void saveHomework_ValidData_SavesEntity() {
        // Arrange
        HomeworkDTO input = HomeworkDTO.builder()
                .name("Math Homework")
                .courseId(1)
                .build();

        Course course = Course.builder().id(1).build();
        Homework savedHomework = Homework.builder().id(1).build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(homeworkRepository.save(any())).thenReturn(savedHomework);
        when(homeworkDTOMapper.apply(any())).thenReturn(input);

        // Act
        HomeworkDTO result = homeworkService.saveHomework(input);

        // Assert
        assertEquals("Math Homework", result.getName());
        verify(homeworkRepository).save(any());
    }

    @Test
    void updateHomework_ValidId_UpdatesFields() {
        // Arrange
        Homework existing = Homework.builder()
                .id(1)
                .name("Old Name")
                .course(Course.builder().id(1).build())
                .build();

        HomeworkDTO update = HomeworkDTO.builder()
                .name("New Name")
                .courseId(1)
                .build();

        when(homeworkRepository.findById(1)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(1)).thenReturn(Optional.of(existing.getCourse()));
        when(homeworkDTOMapper.apply(any())).thenReturn(update);

        // Act
        HomeworkDTO result = homeworkService.updateHomework(1, update);

        // Assert
        assertEquals("New Name", result.getName());
        verify(homeworkRepository).save(existing);
    }

    @Test
    void deleteHomework_ExistingId_DeletesEntity() {
        // Arrange
        Homework homework = Homework.builder().id(1).build();
        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));

        // Act
        homeworkService.deleteHomework(1);

        // Assert
        verify(homeworkRepository).delete(homework);
    }

    @Test
    void addAnswerToHomework_ValidIds_AddsAnswer() {
        // Arrange
        Homework homework = Homework.builder()
                .id(1)
                .answers(new ArrayList<>()) // Инициализация списка
                .build();

        Answer answer = Answer.builder().id(1).build();

        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));
        when(answerRepository.findById(1)).thenReturn(Optional.of(answer));
        when(homeworkDTOMapper.apply(any())).thenReturn(new HomeworkDTO());

        // Act
        HomeworkDTO result = homeworkService.addAnswerToHomework(1, 1);

        // Assert
        assertNotNull(result);
        verify(homeworkRepository).save(homework); // Проверка сохранения
        assertEquals(1, homework.getAnswers().size()); // Проверка добавления ответа
    }

    @Test
    void getAnswers_ValidHomework_ReturnsAnswers() {
        // Arrange
        Homework homework = Homework.builder()
                .answers(List.of(Answer.builder().build()))
                .build();

        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));
        when(answerDTOMapper.apply(any())).thenReturn(new AnswerDTO());

        // Act
        List<AnswerDTO> result = homeworkService.getAnswers(1);

        // Assert
        assertFalse(result.isEmpty());
    }

    @Test
    void deleteAnswer_ValidIds_RemovesAnswer() {
        // Arrange
        int homeworkId = 1;
        int answerId = 1;

        Answer answer = Answer.builder().id(answerId).build();
        Homework homework = Homework.builder()
                .id(homeworkId)
                .answers(new ArrayList<>(List.of(answer)))
                .build();

        when(homeworkRepository.findById(homeworkId)).thenReturn(Optional.of(homework));

        // Act
        homeworkService.deleteAnswer(homeworkId, answerId);

        // Assert
        assertTrue(homework.getAnswers().isEmpty(), "Ответ не был удален");
        verify(homeworkRepository, times(1)).save(homework); // Проверка вызова save
    }

    @Test
    void saveHomework_NullCourseId_ThrowsValidationException() {
        // Arrange
        HomeworkDTO input = HomeworkDTO.builder()
                .name("Invalid Homework")
                .courseId(null)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.saveHomework(input));
    }

    @Test
    void updateHomework_NonExistingHomework_ThrowsException() {
        // Arrange
        HomeworkDTO update = HomeworkDTO.builder().courseId(1).build();
        when(homeworkRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.updateHomework(999, update));
    }

    @Test
    void updateHomework_NonExistingCourse_ThrowsException() {
        // Arrange
        Homework existing = Homework.builder().id(1).build();
        HomeworkDTO update = HomeworkDTO.builder().courseId(999).build();

        when(homeworkRepository.findById(1)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.updateHomework(1, update));
    }

    @Test
    void addAnswerToHomework_NonExistingHomework_ThrowsException() {
        // Arrange
        when(homeworkRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.addAnswerToHomework(999, 1));
    }

    @Test
    void addAnswerToHomework_NonExistingAnswer_ThrowsException() {
        // Arrange
        Homework homework = Homework.builder().id(1).build();
        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));
        when(answerRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.addAnswerToHomework(1, 999));
    }

    @Test
    void deleteAnswer_NonExistingHomework_ThrowsException() {
        // Arrange
        when(homeworkRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.deleteAnswer(999, 1));
    }

    @Test
    void getCurriculum_HomeworkWithoutCourse_ThrowsException() {
        // Arrange
        Homework homework = Homework.builder().id(1).course(null).build();
        when(homeworkRepository.findById(1)).thenReturn(Optional.of(homework));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> homeworkService.getCurriculum(1));
    }

}
