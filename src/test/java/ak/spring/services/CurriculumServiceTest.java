package ak.spring.services;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Course;
import ak.spring.models.Curriculum;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceTest {

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CurriculumDTOMapper curriculumDTOMapper;

    @Mock
    private CourseDTOMapper courseDTOMapper;

    @Mock
    private ClassroomDTOMapper classroomDTOMapper;

    @InjectMocks
    private CurriculumService curriculumService;

    @Test
    void getAllCurricula_ReturnsList() {
        // Arrange
        Curriculum curriculum = Curriculum.builder().id(1).build();
        when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
        when(curriculumDTOMapper.apply(any())).thenReturn(new CurriculumDTO());

        // Act
        List<CurriculumDTO> result = curriculumService.getAllCurricula();

        // Assert
        assertEquals(1, result.size());
        verify(curriculumRepository).findAll();
    }

    @Test
    void getCurriculumById_ExistingId_ReturnsDTO() {
        // Arrange
        Curriculum curriculum = Curriculum.builder().id(1).build();
        when(curriculumRepository.findById(1)).thenReturn(Optional.of(curriculum));
        when(curriculumDTOMapper.apply(any())).thenReturn(new CurriculumDTO());

        // Act
        CurriculumDTO result = curriculumService.getCurriculumById(1);

        // Assert
        assertNotNull(result);
        verify(curriculumRepository).findById(1);
    }

    @Test
    void getCurriculumById_NonExistingId_ThrowsException() {
        // Arrange
        when(curriculumRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> curriculumService.getCurriculumById(999));
    }

    @Test
    void saveCurriculum_ValidEntity_ReturnsDTO() {
        // Arrange
        Curriculum curriculum = Curriculum.builder().name("Test").build();
        when(curriculumRepository.save(any())).thenReturn(curriculum);
        when(curriculumDTOMapper.apply(any())).thenReturn(new CurriculumDTO());

        // Act
        CurriculumDTO result = curriculumService.saveCurriculum(curriculum);

        // Assert
        assertNotNull(result);
        verify(curriculumRepository).save(curriculum);
    }

    @Test
    void deleteCurriculum_ExistingId_DeletesEntity() {
        // Act
        curriculumService.deleteCurriculum(1);

        // Assert
        verify(curriculumRepository).deleteById(1);
    }

    @Test
    void getCourseById_ValidCurriculum_ReturnsCourses() {
        // Arrange
        Curriculum curriculum = Curriculum.builder()
                .courses(List.of(new Course()))
                .build();
        when(curriculumRepository.findById(1)).thenReturn(Optional.of(curriculum));
        when(courseDTOMapper.apply(any())).thenReturn(new CourseDTO());

        // Act
        List<CourseDTO> result = curriculumService.getCourseById(1);

        // Assert
        assertFalse(result.isEmpty());
        verify(curriculumRepository).findById(1);
    }

    @Test
    void updateCurriculum_ValidId_UpdatesFields() {
        // Arrange
        Curriculum existing = Curriculum.builder().id(1).name("Old").build();
        CurriculumDTO update = new CurriculumDTO();
        update.setName("New");
        update.setDescription("Desc");
        update.setAccess(true);

        when(curriculumRepository.findById(1)).thenReturn(Optional.of(existing));
        when(curriculumDTOMapper.apply(any())).thenReturn(update);

        // Act
        CurriculumDTO result = curriculumService.updateCurriculum(1, update);

        // Assert
        assertEquals("New", result.getName());
        verify(curriculumRepository).save(existing);
    }

    @Test
    void addClassroomToCurriculum_ValidIds_AddsClassroom() {
        // Arrange
        Curriculum curriculum = Curriculum.builder().id(1).build();
        Classroom classroom = Classroom.builder().id(1).build();

        when(curriculumRepository.findById(1)).thenReturn(Optional.of(curriculum));
        when(classroomRepository.findById(1)).thenReturn(Optional.of(classroom));
        when(curriculumDTOMapper.apply(any())).thenReturn(new CurriculumDTO());

        // Act
        CurriculumDTO result = curriculumService.addClassroomToCurriculum(1, 1);

        // Assert
        assertNotNull(result);
        verify(curriculumRepository).save(curriculum);
    }

    @Test
    void deleteClassroomFromCurriculum_ValidIds_RemovesClassroom() {
        // Arrange
        int curriculumId = 1;
        int classroomId = 1;

        // Если метод внутри deleteClassroomFromCurriculum вызывает personRepository.findByClassroomId(),
        // то необходимо задать для него поведение. Предположим, что он должен возвращать пустой список:
        when(personRepository.findByClassroomId(classroomId))
                .thenReturn(Collections.emptyList());

        // Создаем объекты с нужными зависимостями
        Classroom classroom = Classroom.builder().id(classroomId).build();
        Curriculum curriculum = Curriculum.builder()
                .id(curriculumId)
                .classrooms(new ArrayList<>(List.of(classroom)))
                .build();

        when(curriculumRepository.findById(curriculumId))
                .thenReturn(Optional.of(curriculum));
        when(classroomRepository.findById(classroomId))
                .thenReturn(Optional.of(classroom));

        // Act
        curriculumService.deleteClassroomFromCurriculum(curriculumId, classroomId);

        // Assert
        assertTrue(curriculum.getClassrooms().isEmpty(), "Класс не был удален из учебного плана");
        verify(curriculumRepository).save(curriculum); // Проверяем, что учебный план сохранен с обновленной коллекцией
    }

    @Test
    void findByName_ExistingName_ReturnsResults() {
        // Arrange
        Curriculum curriculum = Curriculum.builder().name("Math").build();
        when(curriculumRepository.findByName("Math")).thenReturn(List.of(curriculum));
        when(curriculumDTOMapper.apply(any())).thenReturn(new CurriculumDTO());

        // Act
        List<CurriculumDTO> result = curriculumService.findByName("Math");

        // Assert
        assertFalse(result.isEmpty());
        verify(curriculumRepository).findByName("Math");
    }
}