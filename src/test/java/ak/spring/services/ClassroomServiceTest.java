package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CurriculumDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.CurriculumDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.models.Curriculum;
import ak.spring.models.Person;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.CurriculumRepository;
import ak.spring.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private ClassroomDTOMapper classroomDTOMapper;

    @Mock
    private CurriculumDTOMapper curriculumDTOMapper;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CurriculumRepository curriculumRepository;

    @InjectMocks
    private ClassroomService classroomService;

    @Test
    void findByName_ExistingName_ReturnsClassroomDTOList() {
        String name = "Math";
        Classroom classroom = Classroom.builder()
                .id(1)
                .name(name)
                .persons(new ArrayList<>())
                .build();
        ClassroomDTO dto = new ClassroomDTO(1, name, List.of());

        when(classroomRepository.findByNameContainingIgnoreCase(name))
                .thenReturn(Optional.of(List.of(classroom)));
        when(classroomDTOMapper.apply(classroom)).thenReturn(dto);

        List<ClassroomDTO> result = classroomService.findByName(name);

        assertFalse(result.isEmpty());
        assertEquals(name, result.get(0).getName());
    }


    @Test
    void findByName_NonExistingName_ThrowsException() {
        // Arrange
        String name = "Unknown";
        when(classroomRepository.findByNameContainingIgnoreCase(name))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> classroomService.findByName(name));
    }

    @Test
    void findById_ValidId_ReturnsClassroomDTO() {
        int id = 1;
        Classroom classroom = Classroom.builder()
                .id(id)
                .name("Math")
                .persons(new ArrayList<>())
                .build();
        ClassroomDTO dto = new ClassroomDTO(id, "Math", List.of());

        when(classroomRepository.findById(id)).thenReturn(Optional.of(classroom));
        when(classroomDTOMapper.apply(classroom)).thenReturn(dto);

        ClassroomDTO result = classroomService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void findById_InvalidId_ThrowsException() {
        // Arrange
        int id = 999;
        when(classroomRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> classroomService.findById(id));
    }

    @Test
    void updateGroup_ValidId_UpdatesName() {
        int id = 1;
        ClassroomDTO updatedDTO = new ClassroomDTO(id, "New Name", List.of());
        Classroom existing = Classroom.builder()
                .id(id)
                .name("Old Name")
                .persons(new ArrayList<>())
                .build();
        ClassroomDTO mappedDTO = new ClassroomDTO(id, "New Name", List.of());

        when(classroomRepository.findById(id)).thenReturn(Optional.of(existing));
        when(classroomRepository.save(any())).thenReturn(existing);
        when(classroomDTOMapper.apply(existing)).thenReturn(mappedDTO);

        ClassroomDTO result = classroomService.updateGroup(id, updatedDTO);

        assertEquals("New Name", result.getName());
    }

    @Test
    void addStudentToClassroom_ValidIds_AddsStudent() {
        int classroomId = 1;
        int studentId = 10;
        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Math")
                .persons(new ArrayList<>())
                .build();
        Person student = Person.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .build();
        ClassroomDTO dto = new ClassroomDTO(classroomId, "Math", List.of(PersonDTO
                .builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .build()));

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
        when(personRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classroomRepository.save(any())).thenReturn(classroom);
        when(classroomDTOMapper.apply(classroom)).thenReturn(dto);

        ClassroomDTO result = classroomService.addStudentToClassroom(classroomId, studentId);

        assertFalse(result.getPersons().isEmpty());
        verify(personRepository).save(student);
    }

    @Test
    void deleteStudentFromClassroom_ValidIds_RemovesStudent() {
        // Arrange
        int classroomId = 1;
        int studentId = 10;
        Person student = Person.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Doe")
                .build();
        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Math")
                .persons(new ArrayList<>(List.of(student)))
                .build();

        when(personRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));

        // Act
        classroomService.deleteStudentFromClassroom(classroomId, studentId);

        // Assert
        assertTrue(classroom.getPersons().isEmpty());
        assertNull(student.getClassroom());
        verify(personRepository).save(student);
    }

    @Test
    void addStudentToClassroom_ExceedsMaxStudents_ThrowsException() {
        int classroomId = 1;
        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Math")
                .persons(new ArrayList<>())
                .build();

        // Добавляем 50 студентов
        for (int i = 1; i <= 50; i++) {
            classroom.getPersons().add(Person.builder().id(i).build());
        }

        Person newStudent = Person.builder().id(51).build();

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
        when(personRepository.findById(51)).thenReturn(Optional.of(newStudent));

        assertThrows(DataIntegrityViolationException.class, () -> {
            classroomService.addStudentToClassroom(classroomId, 51);
        });
    }

    @Test
    void getCurriculum_ClassroomHasCurriculum_ReturnsCurriculumDTO() {
        int classroomId = 1;
        Curriculum curriculum = Curriculum.builder()
                .id(1)
                .name("Math Curriculum")
                .build();
        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Math")
                .curriculum(curriculum)
                .build();
        CurriculumDTO dto = CurriculumDTO.builder()
                .id(1)
                .name("Math Curriculum")
                .build();

        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
        when(curriculumDTOMapper.apply(curriculum)).thenReturn(dto);

        CurriculumDTO result = classroomService.getCurriculum(classroomId);

        assertEquals(curriculum.getId(), result.getId());
    }

    @Test
    void uploadGroup_ValidClassroom_ReturnsDTO() {
        // Arrange
        Classroom input = Classroom.builder()
                .name("New Class")
                .build();

        // Создаем объект, который будет возвращен при сохранении
        Classroom savedEntity = Classroom.builder()
                .id(1)
                .name("New Class")
                .build();

        // DTO, которое должен вернуть маппер
        ClassroomDTO expectedDTO = ClassroomDTO.builder()
                .id(1)
                .name("New Class")
                .build();

        // Настройка моков
        when(classroomRepository.save(any(Classroom.class))).thenReturn(savedEntity);
        when(classroomDTOMapper.apply(any(Classroom.class))).thenReturn(expectedDTO);

        // Act
        ClassroomDTO result = classroomService.uploadGroup(input);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("New Class", result.getName());

        // Проверка вызовов
        verify(classroomRepository).save(any(Classroom.class));
        verify(classroomDTOMapper).apply(any(Classroom.class));
    }

    @Test
    void deleteGroup_ValidId_DeletesClassroom() {
        // Arrange
        int id = 1;
        Classroom classroom = Classroom.builder()
                .id(id)
                .name("Math")
                .persons(new ArrayList<>())
                .build();
        when(classroomRepository.findById(id)).thenReturn(Optional.of(classroom));

        // Act
        classroomService.deleteGroup(id);

        // Assert
        verify(classroomRepository).delete(classroom);
    }

    @Test
    void addClassroomToCurriculum_ValidIds_UpdatesCurriculum() {
        // Arrange
        int classroomId = 1;
        int curriculumId = 10;

        Curriculum curriculum = Curriculum.builder()
                .id(curriculumId)
                .name("Math Curriculum")
                .build();

        Classroom classroom = Classroom.builder()
                .id(classroomId)
                .name("Math Class")
                .persons(new ArrayList<>()) // Инициализация списка
                .curriculum(null) // Изначально учебный план не привязан
                .build();

        // Настройка моков репозиториев
        when(classroomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
        when(curriculumRepository.findById(curriculumId)).thenReturn(Optional.of(curriculum));

        // Настройка маппера
        ClassroomDTO expectedDTO = ClassroomDTO.builder()
                .id(classroomId)
                .name("Math Class")
                .persons(new ArrayList<>())
                .build();

        when(classroomDTOMapper.apply(classroom)).thenReturn(expectedDTO);

        // Act
        ClassroomDTO result = classroomService.addClassroomToCurriculum(classroomId, curriculumId);

        // Assert
        assertNotNull(result, "DTO не должен быть null");
        assertEquals(classroomId, result.getId(), "ID класса должно совпадать");
        assertEquals("Math Class", result.getName(), "Название класса должно совпадать");
        verify(classroomRepository).save(classroom); // Проверка сохранения
    }
}