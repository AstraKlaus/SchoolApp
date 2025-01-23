package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.Course;
import ak.spring.models.Lesson;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.LessonRepository;
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
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonDTOMapper lessonDTOMapper;

    @Mock
    private CourseDTOMapper courseDTOMapper;

    @InjectMocks
    private LessonService lessonService;

    // Тест для успешного поиска урока по ID
    @Test
    void findById_ExistingId_ReturnsLessonDTO() {
        // Arrange
        Lesson lesson = Lesson.builder()
                .id(1)
                .name("Introduction to Java")
                .build();

        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(lessonDTOMapper.apply(lesson)).thenReturn(new LessonDTO());

        // Act
        LessonDTO result = lessonService.findById(1);

        // Assert
        assertNotNull(result);
        verify(lessonRepository).findById(1);
    }

    // Тест для случая, когда урок не найден
    @Test
    void findById_NonExistingId_ThrowsException() {
        // Arrange
        when(lessonRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> lessonService.findById(999));
    }

    // Тест для сохранения урока
    @Test
    void saveLesson_ValidData_ReturnsLessonDTO() {
        // Arrange
        LessonDTO input = LessonDTO.builder()
                .name("OOP Principles")
                .courseId(1)
                .build();

        Course course = Course.builder().id(1).build();
        Lesson savedLesson = Lesson.builder().id(1).build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any())).thenReturn(savedLesson);
        when(lessonDTOMapper.apply(any())).thenReturn(input);

        // Act
        LessonDTO result = lessonService.saveLesson(input);

        // Assert
        assertEquals("OOP Principles", result.getName());
        verify(lessonRepository).save(any());
    }

    // Тест для обновления урока
    @Test
    void updateLesson_ValidId_UpdatesFields() {
        // Arrange
        Lesson existing = Lesson.builder()
                .id(1)
                .name("Old Name")
                .build();

        LessonDTO update = LessonDTO.builder()
                .name("New Name")
                .build();

        when(lessonRepository.findById(1)).thenReturn(Optional.of(existing));
        when(lessonDTOMapper.apply(any())).thenReturn(update);

        // Act
        LessonDTO result = lessonService.updateLesson(1, update);

        // Assert
        assertEquals("New Name", result.getName());
        verify(lessonRepository).save(existing);
    }

    // Тест для добавления курса к уроку
    @Test
    void addCourseToLesson_ValidIds_UpdatesCourse() {
        // Arrange
        Lesson lesson = Lesson.builder().id(1).build();
        Course course = Course.builder().id(1).build();

        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lessonDTOMapper.apply(any())).thenReturn(new LessonDTO());

        // Act
        LessonDTO result = lessonService.addCourseToLesson(1, 1);

        // Assert
        assertNotNull(result);
        verify(lessonRepository).save(lesson);
    }

    // Тест для поиска по имени с пагинацией
    @Test
    void findWithPagination_ReturnsPage() {
        // Arrange
        Page<Lesson> page = new PageImpl<>(List.of(new Lesson()));
        when(lessonRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(lessonDTOMapper.apply(any())).thenReturn(new LessonDTO());

        // Act
        Page<LessonDTO> result = lessonService.findWithPagination(0, 10);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    // Тест для получения курса урока
    @Test
    void getCourse_ValidLesson_ReturnsCourseDTO() {
        // Arrange
        Course course = Course.builder().id(1).build();
        Lesson lesson = Lesson.builder().course(course).build();

        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(courseDTOMapper.apply(course)).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = lessonService.getCourse(1);

        // Assert
        assertNotNull(result);
    }
}
