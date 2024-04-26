package ak.spring.services;

import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.models.Enrollment;
import ak.spring.models.Person;
import ak.spring.models.Course;
import ak.spring.models.Role;
import ak.spring.repositories.PersonRepository;
import ak.spring.repositories.CourseRepository;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PersonService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final PersonRepository personRepository;
    private final CourseService courseService;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository, PersonRepository personRepository, CourseRepository courseRepository, CourseService courseService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.personRepository = personRepository;
        this.courseService = courseService;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findByUsername(String name) {
        return personRepository.findByUsername(name);
    }

    public Person findByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::getUser)
                .orElse(null);
    }

    public Person findById(int id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
    }

    public Person uploadPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    public void deletePerson(Person person) {
        personRepository.delete(person);
    }

    public Person update(int id, Person updatedPerson) {
        Person existingPerson = findById(id);
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setLastName(updatedPerson.getLastName());
        return personRepository.save(existingPerson);
    }

    public List<Course> findCoursesForPerson(int personId) {
        return findById(personId).getCourses(); }

    public void enrollPersonInCourse(int personId, int courseId) {
        Person person = findById(personId);
        Course course = courseService.findById(courseId);

        Enrollment enrollment = Enrollment.builder().person(person).course(course).build();
        person.getEnrollments().add(enrollment);
        personRepository.save(person);
    }

    public void removePersonFromCourse(int personId, int courseId) {
        Person person = findById(personId);
        Course course = courseService.findById(courseId);

        Enrollment enrollment = person.getEnrollments().stream()
                .filter(e -> e.getCourse().equals(course))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "personId, courseId", personId + ", " + courseId));

        person.getEnrollments().remove(enrollment);
        personRepository.save(person);
    }

    public void assignRoleToUser(int userId, Role role) {
        Person person = findById(userId);
        person.setRole(role);
        personRepository.save(person);
    }

    public void removeRoleFromUser(int userId, Role role) {
        Person person = findById(userId);
        if (person.getRole() == role) {
            person.setRole(Role.USER);
            personRepository.save(person);
        } else {
            throw new IllegalArgumentException("Пользователь не может иметь эту роль");
        }
    }
}
