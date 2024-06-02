package ak.spring;
import ak.spring.dto.LessonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Course;
import ak.spring.models.Lesson;
import ak.spring.repositories.LessonRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson lesson;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = Course.builder().id(1).name("Java").build();

        lesson = Lesson.builder()
                .id(1)
                .name("Lesson 1")
                .content("Introduction to Java")
                .course(course)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Test
    void findByName_ShouldReturnLessons() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonRepository.findByNameContainingIgnoreCase("Lesson 1")).thenReturn(Optional.of(lessons));

        List<LessonDTO> result = lessonService.findByName("Lesson 1");

        assertEquals(1, result.size());
        assertEquals(lesson.getName(), result.get(0).getName());
    }

    @Test
    void findByName_ShouldThrowException() {
        when(lessonRepository.findByNameContainingIgnoreCase("Invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.findByName("Invalid"));
    }

    @Test
    void findWithPagination_ShouldReturnPageOfLessons() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        Page<Lesson> page = new PageImpl<>(lessons);
        when(lessonRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<LessonDTO> result = lessonService.findWithPagination(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(lesson.getName(), result.getContent().get(0).getName());
    }

    @Test
    void findById_ShouldReturnLesson() {
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

        Lesson result = lessonRepository.findById(1).get();

        assertEquals(lesson, result);
    }

    @Test
    void findById_ShouldThrowException() {
        when(lessonRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.findById(2));
    }

    @Test
    void deleteLesson_ShouldDeleteLesson() {
        lessonService.deleteLesson(1);

        verify(lessonRepository, times(1)).delete(lesson);
    }

    @Test
    void saveLesson_ShouldSaveLesson() {
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson newLesson = Lesson.builder()
                .name("New Lesson")
                .content("Introduction to Java")
                .course(course)
                .build();

        Lesson result = lessonService.saveLesson(newLesson);

        assertEquals(lesson, result);
    }

    @Test
    void updateLesson_ShouldUpdateLesson() {
        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        Lesson updatedLesson = Lesson.builder()
                .name("Updated Lesson")
                .content("Updated Content")
                .course(course)
                .build();

        Lesson result = lessonService.updateLesson(1, updatedLesson);

        assertEquals(lesson.getName(), result.getName());
        assertEquals(lesson.getContent(), result.getContent());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void findAll_ShouldReturnAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonRepository.findAll()).thenReturn(lessons);

        List<Lesson> result = lessonRepository.findAll();

        assertEquals(1, result.size());
        assertEquals(lesson, result.get(0));
    }
}
