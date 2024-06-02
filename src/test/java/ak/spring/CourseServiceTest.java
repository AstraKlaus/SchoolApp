package ak.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.models.Course;
import ak.spring.models.Enrollment;
import ak.spring.models.Person;
import ak.spring.repositories.CourseRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CourseDTOMapper courseDTOMapper;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findAll_shouldReturnListOfCourseDTO() {
        // Arrange
        Course course = new Course();
        List<Course> courses = List.of(course);
        CourseDTO courseDTO = new CourseDTO();
        when(courseRepository.findAll()).thenReturn(courses);
        when(courseDTOMapper.apply(course)).thenReturn(courseDTO);

        // Act
        List<CourseDTO> result = courseService.findAll();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findWithPagination_shouldReturnPageOfCourseDTO() {
        // Arrange
        int offset = 0;
        int pageSize = 10;
        Course course = new Course();
        List<Course> courses = List.of(course);
        Page<Course> coursePage = new PageImpl<>(courses);
        CourseDTO courseDTO = new CourseDTO();
        when(courseRepository.findAll(PageRequest.of(offset, pageSize))).thenReturn(coursePage);
        when(courseDTOMapper.apply(course)).thenReturn(courseDTO);

        // Act
        Page<CourseDTO> result = courseService.findWithPagination(offset, pageSize);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByName_shouldReturnListOfCourseDTO() {
        // Arrange
        String name = "test";
        Course course = new Course();
        course.setName(name);
        List<Course> courses = List.of(course);
        CourseDTO courseDTO = new CourseDTO();
        when(courseRepository.findByNameContainingIgnoreCase(name)).thenReturn(Optional.of(courses));
        when(courseDTOMapper.apply(course)).thenReturn(courseDTO);

        // Act
        List<CourseDTO> result = courseService.findByName(name);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void uploadCourse_shouldSaveAndReturnCourse() {
        // Arrange
        Course course = new Course();
        course.setName("new course");
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course result = courseService.uploadCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals("new course", result.getName());
    }

    @Test
    void findById_shouldReturnCourseDTO() {
        // Arrange
        int id = 1;
        Course course = new Course();
        course.setId(id);
        CourseDTO courseDTO = new CourseDTO();
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(courseDTOMapper.apply(course)).thenReturn(courseDTO);

        // Act
        CourseDTO result = courseService.findById(id);

        // Assert
        assertNotNull(result);
    }

    @Test
    void deleteCourse_shouldDeleteCourse() {
        // Arrange
        int id = 1;
        Course course = new Course();
        course.setId(id);
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        // Act
        courseService.deleteCourse(id);

        // Assert
        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void updateCourse_shouldUpdateAndReturnCourse() {
        // Arrange
        int id = 1;
        Course existingCourse = new Course();
        existingCourse.setId(id);
        Course updatedCourse = new Course();
        updatedCourse.setName("updated name");
        updatedCourse.setDescription("updated description");
        when(courseRepository.findById(id)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(existingCourse)).thenReturn(existingCourse);

        // Act
        Course result = courseService.updateCourse(id, updatedCourse);

        // Assert
        assertNotNull(result);
        assertEquals("updated name", result.getName());
        assertEquals("updated description", result.getDescription());
    }

    @Test
    void getStudents_shouldReturnListOfPersonDTO() {
        // Arrange
        int id = 1;
        Course course = new Course();
        course.setId(id);
        Person student = new Person();
        List<Person> students = List.of(student);
        course.setStudents(students);
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setStudents(List.of(new PersonDTO()));
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));
        when(courseDTOMapper.apply(course)).thenReturn(courseDTO);

        // Act
        List<PersonDTO> result = courseService.getStudents(id);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void addPersonInCourse_shouldEnrollPersonInCourse() {
        int id = 1;
        Course course = new Course();
        course.setId(id);
        Person student = new Person();
        student.setId(id);
        List<Person> students = List.of(student);

        when(personRepository.findById(id)).thenReturn(Optional.of(student));
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        courseService.addPersonInCourse(id, id);

        verify(personRepository).save(student);
        assertTrue(student.getCourses().contains(course));
    }

    @Test
    void removePersonFromCourse_shouldRemovePersonFromCourse() {
        int id = 1;
        Course course = new Course();
        course.setId(id);
        Person student = new Person();
        student.setId(id);
        student.setCourses(new ArrayList<>(Collections.singletonList(course)));
        List<Person> students = List.of(student);

        when(personRepository.findById(id)).thenReturn(Optional.of(student));
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        courseService.removePersonFromCourse(id, id);

        verify(personRepository).save(student);
        assertFalse(student.getCourses().contains(course));
    }

}