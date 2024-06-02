package ak.spring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Person;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.ClassroomService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private ClassroomDTOMapper classroomDTOMapper;

    @InjectMocks
    private ClassroomService classroomService;

    @Test
    void findByName_shouldReturnClassroomDTOList() {
        // Arrange
        String name = "test";
        Classroom classroom = new Classroom();
        classroom.setName(name);
        List<Classroom> classrooms = List.of(classroom);
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findByNameContainingIgnoreCase(name)).thenReturn(Optional.of(classrooms));
        when(classroomDTOMapper.apply(classroom)).thenReturn(classroomDTO);

        // Act
        List<ClassroomDTO> result = classroomService.findByName(name);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void findWithPagination_shouldReturnPageOfClassroomDTO() {
        // Arrange
        int offset = 0;
        int pageSize = 10;
        Classroom classroom = new Classroom();
        List<Classroom> classrooms = List.of(classroom);
        Page<Classroom> classroomPage = new PageImpl<>(classrooms);
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findAll(PageRequest.of(offset, pageSize))).thenReturn(classroomPage);
        when(classroomDTOMapper.apply(classroom)).thenReturn(classroomDTO);

        // Act
        Page<ClassroomDTO> result = classroomService.findWithPagination(offset, pageSize);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findById_shouldReturnClassroomDTO() {
        // Arrange
        int id = 1;
        Classroom classroom = new Classroom();
        classroom.setId(id);
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findById(id)).thenReturn(Optional.of(classroom));
        when(classroomDTOMapper.apply(classroom)).thenReturn(classroomDTO);

        // Act
        ClassroomDTO result = classroomService.findById(id);

        // Assert
        assertNotNull(result);
    }

    @Test
    void updateGroup_shouldUpdateAndReturnClassroom() {
        // Arrange
        int id = 1;
        Classroom existingClassroom = new Classroom();
        existingClassroom.setId(id);
        Classroom updatedClassroom = new Classroom();
        updatedClassroom.setName("updated name");
        when(classroomRepository.findById(id)).thenReturn(Optional.of(existingClassroom));
        when(classroomRepository.save(existingClassroom)).thenReturn(existingClassroom);

        // Act
        Classroom result = classroomService.updateGroup(id, updatedClassroom);

        // Assert
        assertNotNull(result);
        assertEquals("updated name", result.getName());
    }

    @Test
    void deleteGroup_shouldDeleteClassroom() {
        // Arrange
        int id = 1;
        Classroom classroom = new Classroom();
        classroom.setId(id);
        when(classroomRepository.findById(id)).thenReturn(Optional.of(classroom));

        // Act
        classroomService.deleteGroup(id);

        // Assert
        verify(classroomRepository, times(1)).delete(classroom);
    }

    @Test
    void uploadGroup_shouldSaveAndReturnClassroom() {
        // Arrange
        Classroom classroom = new Classroom();
        classroom.setName("new classroom");
        when(classroomRepository.save(any(Classroom.class))).thenReturn(classroom);

        // Act
        Classroom result = classroomService.uploadGroup(classroom);

        // Assert
        assertNotNull(result);
        assertEquals("new classroom", result.getName());
    }

    @Test
    void findAll_shouldReturnListOfClassroomDTO() {
        // Arrange
        Classroom classroom = new Classroom();
        List<Classroom> classrooms = List.of(classroom);
        ClassroomDTO classroomDTO = new ClassroomDTO();
        when(classroomRepository.findAll()).thenReturn(classrooms);
        when(classroomDTOMapper.apply(classroom)).thenReturn(classroomDTO);

        // Act
        List<ClassroomDTO> result = classroomService.findAll();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}