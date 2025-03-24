package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.HomeworkDTO;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.mappers.HomeworkDTOMapper;
import ak.spring.mappers.LessonDTOMapper;
import ak.spring.models.*;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.HomeworkRepository;
import ak.spring.repositories.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private HomeworkRepository homeworkRepository;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private CourseDTOMapper courseDTOMapper;

    @Mock
    private LessonDTOMapper lessonDTOMapper;

    @Mock
    private HomeworkDTOMapper homeworkDTOMapper;

    @Mock
    private CurriculumDTOMapper curriculumDTOMapper;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findAll_ReturnsAllCourses() {
        // Arrange
        Course course = Course.builder().id(1).build();
        when(courseRepository.findAll(any(Sort.class))).thenReturn(List.of(course));
        when(courseDTOMapper.apply(any())).thenReturn(new CourseDTO());

        // Act
        List<CourseDTO> result = courseService.findAll();

        // Assert
        assertEquals(1, result.size());
        verify(courseRepository).findAll(Sort.by("id").ascending());
    }


    @Test
    void findById_ExistingId_ReturnsCourseDTO() {
        // Arrange
        Course course = Course.builder().id(1).build();
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseDTOMapper.apply(any())).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.findById(1);

        // Assert
        assertNotNull(result);
        verify(courseRepository).findById(1);
    }

    @Test
    void findById_NonExistingId_ThrowsException() {
        // Arrange
        when(courseRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> courseService.findById(999));
    }

    @Test
    void uploadCourse_ValidData_SavesCourse() {
        // Arrange
        CourseDTO input = CourseDTO.builder()
                .name("Math")
                .curriculumId(1)
                .build();

        Curriculum curriculum = Curriculum.builder().id(1).build();
        Course savedCourse = Course.builder().id(1).build();

        when(curriculumRepository.findById(1)).thenReturn(Optional.of(curriculum));
        when(courseRepository.save(any())).thenReturn(savedCourse);
        when(courseDTOMapper.apply(any())).thenReturn(input);

        // Act
        CourseDTO result = courseService.uploadCourse(input);

        // Assert
        assertEquals("Math", result.getName());
        verify(courseRepository).save(any());
    }

    @Test
    void updateCourse_ValidId_UpdatesFields() {
        // Arrange
        Course existing = Course.builder().id(1).name("Old").build();
        CourseDTO update = CourseDTO.builder().name("New").build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(existing));
        when(courseDTOMapper.apply(any())).thenReturn(update);

        // Act
        CourseDTO result = courseService.updateCourse(1, update);

        // Assert
        assertEquals("New", result.getName());
        verify(courseRepository).save(existing);
    }

    @Test
    void addLessonToCourse_ValidIds_AddsLesson() {
        // Arrange
        Course course = Course.builder().id(1).build();
        Lesson lesson = Lesson.builder().id(1).build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(courseDTOMapper.apply(any())).thenReturn(new CourseDTO());

        // Act
        CourseDTO result = courseService.addLessonToCourse(1, 1);

        // Assert
        assertNotNull(result);
        verify(courseRepository).save(course);
    }

    @Test
    void deleteCurriculumFromCourse_ValidId_RemovesCurriculum() {
        // Arrange
        Course course = Course.builder()
                .id(1)
                .curriculum(Curriculum.builder().id(1).build())
                .build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        // Act
        courseService.deleteCurriculumFromCourse(1, 1);

        // Assert
        assertNull(course.getCurriculum());
        verify(courseRepository).save(course);
    }

    @Test
    void getHomeworks_ValidCourse_ReturnsHomeworks() {
        // Arrange
        Course course = Course.builder()
                .homeworks(List.of(Homework.builder().build()))
                .build();

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(homeworkDTOMapper.apply(any())).thenReturn(new HomeworkDTO());

        // Act
        List<HomeworkDTO> result = courseService.getHomeworks(1);

        // Assert
        assertFalse(result.isEmpty());
    }

    // Добавьте остальные тесты по аналогии
}
