package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.dto.ClassroomDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.models.Classroom;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.token.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonDTOMapper personDTOMapper;

    @Mock
    private ClassroomDTOMapper classroomDTOMapper;

    private Person person;
    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        person = new Person();
        person.setId(1);
        person.setUsername("testUser");
        person.setFirstName("Иван");
        person.setLastName("Петров");
        person.setRole(Role.STUDENT);
        person.setPassword("password");

        personDTO = new PersonDTO();
        personDTO.setId(1);
        personDTO.setUsername("testUser");
        personDTO.setFirstName("Иван");
        personDTO.setLastName("Петров");
        personDTO.setRole(Role.STUDENT);
    }

    @Test
    void testFindAllSuccess() {
        when(personRepository.findAll()).thenReturn(List.of(person));
        when(personDTOMapper.apply(any(Person.class))).thenReturn(personDTO);

        List<PersonDTO> result = personService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testFindByUsernameSuccess() {
        when(personRepository.findByUsername("testUser")).thenReturn(Optional.of(person));
        when(personDTOMapper.apply(person)).thenReturn(personDTO);

        PersonDTO result = personService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(personRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindByUsernameNotFound() {
        when(personRepository.findByUsername("notExist")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            personService.findByUsername("notExist");
        });

        verify(personRepository, times(1)).findByUsername("notExist");
    }

    @Test
    void testFindByIdSuccess() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person));
        when(personDTOMapper.apply(person)).thenReturn(personDTO);

        PersonDTO result = personService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        when(personRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            personService.findById(999);
        });

        verify(personRepository, times(1)).findById(999);
    }

    @Test
    void testUploadPersonSuccess() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personDTOMapper.apply(person)).thenReturn(personDTO);

        PersonDTO result = personService.uploadPerson(person);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void testDeletePersonSuccess() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person));
        doNothing().when(personRepository).delete(person);

        assertDoesNotThrow(() -> personService.deletePerson(1));

        verify(personRepository, times(1)).delete(person);
    }

    @Test
    void testDeletePersonNotFound() {
        when(personRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            personService.deletePerson(999);
        });

        verify(personRepository, times(1)).findById(999);
    }

    @Test
    void testUpdatePersonSuccess() {
        PersonDTO updatedDTO = new PersonDTO();
        updatedDTO.setUsername("updatedUser");
        updatedDTO.setFirstName("Сергей");
        updatedDTO.setLastName("Сергеев");
        updatedDTO.setRole(Role.ADMIN);

        when(personRepository.findById(1)).thenReturn(Optional.of(person));
        when(personRepository.save(any(Person.class))).thenReturn(person);
        when(personDTOMapper.apply(person)).thenReturn(updatedDTO);

        PersonDTO result = personService.update(1, updatedDTO);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testUpdatePersonNotFound() {
        PersonDTO updatedDTO = new PersonDTO();
        updatedDTO.setUsername("updatedUser");

        when(personRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            personService.update(999, updatedDTO);
        });

        verify(personRepository, times(1)).findById(999);
    }

    @Test
    void testFindClassroomForPersonSuccess() {
        // Создание тестового объекта Classroom
        Classroom classroom = new Classroom();
        classroom.setId(101);

        // Установка classroom в объект person
        person.setClassroom(classroom);

        // Мокируем вызовы репозитория и маппера
        when(personRepository.findById(1)).thenReturn(Optional.of(person));
        when(classroomDTOMapper.apply(classroom)).thenReturn(new ClassroomDTO());

        // Проверка, что метод не выбрасывает исключения
        ClassroomDTO result = assertDoesNotThrow(() -> personService.findClassroomForPerson(1));

        assertNotNull(result);
        verify(personRepository, times(1)).findById(1);
        verify(classroomDTOMapper, times(1)).apply(classroom);
    }

    @Test
    void testFindClassroomForPersonNotFound() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person));
        person.setClassroom(null);

        // Проверка выбрасывания исключения при отсутствии класса
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                personService.findClassroomForPerson(1)
        );

        assertEquals("Classroom", exception.getResourceName());
        assertEquals("personId", exception.getFieldName());
        assertEquals(1, exception.getFieldValue());

        verify(personRepository, times(1)).findById(1);
        verifyNoInteractions(classroomDTOMapper);
    }

    @Test
    void testFindClassroomForPersonPersonNotFound() {
        when(personRepository.findById(999)).thenReturn(Optional.empty());

        // Проверка выбрасывания исключения при отсутствии пользователя
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                personService.findClassroomForPerson(999)
        );

        assertEquals("Person", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(999, exception.getFieldValue());

        verify(personRepository, times(1)).findById(999);
        verifyNoInteractions(classroomDTOMapper);
    }


    @Test
    void testAssignRoleToUserSuccess() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person));

        assertDoesNotThrow(() -> personService.assignRoleToUser(1, Role.ADMIN));

        verify(personRepository, times(1)).save(person);
    }

    @Test
    void testAssignRoleToUserNotFound() {
        when(personRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            personService.assignRoleToUser(999, Role.ADMIN);
        });

        verify(personRepository, times(1)).findById(999);
    }
}
