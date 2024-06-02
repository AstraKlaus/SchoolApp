package ak.spring;

import ak.spring.dto.CourseDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.CourseDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Course;
import ak.spring.models.Enrollment;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.repositories.PersonRepository;
import ak.spring.services.CourseService;
import ak.spring.services.PersonService;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private PersonService personService;

    private Person person1;
    private Person person2;
    private Course course;
    private CourseDTOMapper courseMapper;

    @BeforeEach
    void setUp() {
        person1 = Person.builder()
                .id(1)
                .username("person1")
                .firstName("John")
                .lastName("Doe")
                .password("password1")
                .courses(new ArrayList<Course>())
                .role(Role.USER)
                .build();

        person2 = Person.builder()
                .id(2)
                .username("person")
                .firstName("Jane")
                .lastName("Smith")
                .password("password2")
                .role(Role.ADMIN)
                .build();

        course = Course.builder()
                .id(1)
                .name("Course 1")
                .build();
    }

    @Test
    void findAll_shouldReturnListOfPersons() {
        List<Person> expectedPersons = Arrays.asList(person1, person2);
        when(personRepository.findAll()).thenReturn(expectedPersons);

        List<Person> actualPersons = personRepository.findAll();

        assertEquals(expectedPersons, actualPersons);
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void findByEmail_shouldReturnPerson_whenPersonExists() {
        when(personRepository.findByUsername("person1")).thenReturn(Optional.of(person1));

        PersonDTO actualPerson = personService.findByUsername("person1");

        assertEquals(person1.getUsername(), actualPerson.getUsername());
        verify(personRepository, times(1)).findByUsername("person1");
    }

    @Test
    void findByEmail_shouldReturnEmptyOptional_whenPersonDoesNotExist() {
        when(personRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        PersonDTO actualPerson = personService.findByUsername("nonexistent");

        verify(personRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void findByToken_shouldReturnPerson_whenTokenExists() {
        Token token = new Token();
        token.setUser(person1);

        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));

        Person actualPerson = personService.findByToken("validToken");

        assertEquals(person1, actualPerson);
        verify(tokenRepository, times(1)).findByToken("validToken");
    }

    @Test
    void findByToken_shouldReturnNull_whenTokenDoesNotExist() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        Person actualPerson = personService.findByToken("invalidToken");

        assertNull(actualPerson);
        verify(tokenRepository, times(1)).findByToken("invalidToken");
    }

    @Test
    void findById_shouldReturnPerson_whenPersonExists() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person1));

        Person actualPerson = personRepository.findById(1).get();

        assertEquals(person1, actualPerson);
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void findById_shouldThrowResourceNotFoundException_whenPersonDoesNotExist() {
        when(personRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> personService.findById(1));
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void uploadPerson_shouldSavePersonWithEncodedPassword() {
        when(passwordEncoder.encode("password1")).thenReturn("encodedPassword1");
        when(personRepository.save(person1)).thenReturn(person1);

        Person savedPerson = personService.uploadPerson(person1);

        assertEquals(person1, savedPerson);
        verify(passwordEncoder, times(1)).encode("password1");
        verify(personRepository, times(1)).save(person1);
    }

    @Test
    void deletePerson_shouldDeletePerson() {
        personService.deletePerson(1);

        verify(personRepository, times(1)).delete(person1);
    }

    @Test
    void update_shouldUpdatePerson_whenPersonExists() {
        Person updatedPerson = Person.builder()
                .id(1)
                .username("person1")
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .password("password1")
                .role(Role.USER)
                .build();

        when(personRepository.findById(1)).thenReturn(Optional.of(person1));
        when(personRepository.save(person1)).thenReturn(updatedPerson);

        Person actualPerson = personService.update(1, updatedPerson);

        assertEquals(updatedPerson, actualPerson);
        verify(personRepository, times(1)).findById(1);
        verify(personRepository, times(1)).save(person1);
    }

    @Test
    void findCoursesForPerson_shouldReturnListOfCourses() {
        List<Course> expectedCourses = Collections.singletonList(course);
        person1.setCourses(expectedCourses);

        when(personRepository.findById(1)).thenReturn(Optional.of(person1));

        List<CourseDTO> actualCourses = personService.findCoursesForPerson(1);

        assertEquals(expectedCourses.stream()
                .map(courseMapper)
                .toList(),
                actualCourses);
        verify(personRepository, times(1)).findById(1);
    }

    @Test
    void assignRoleToUser_shouldAssignRoleToUser() {
        when(personRepository.findById(1)).thenReturn(Optional.of(person1));
        when(personRepository.save(person1)).thenReturn(person1);

        personService.assignRoleToUser(1, Role.USER);

        assertEquals(Role.USER, person1.getRole());
        verify(personRepository, times(1)).findById(1);
        verify(personRepository, times(1)).save(person1);
    }
}
