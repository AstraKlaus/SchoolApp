package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.CourseDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.models.Settings;
import ak.spring.repositories.PersonRepository;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final PersonRepository personRepository;
    private final PersonDTOMapper personDTOMapper;
    private final ClassroomDTOMapper classroomDTOMapper;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository,
                         PersonRepository personRepository,
                         PersonDTOMapper personDTOMapper,
                         ClassroomDTOMapper classroomDTOMapper) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.classroomDTOMapper = classroomDTOMapper;
    }

    public List<PersonDTO> findAll() {
        return personRepository.findAll()
                .stream()
                .map(personDTOMapper)
                .toList();
    }

    public PersonDTO findByUsername(String name) {
        return personRepository.findByUsername(name).map(personDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", name));
    }

    public Person findByToken(String token) {
        return tokenRepository.findByToken(token)
                .map(Token::getUser)
                .orElse(null);
    }

    public PersonDTO findById(int id) {
        return personRepository.findById(id).map(personDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
    }

    public Person uploadPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    public void deletePerson(int id) {
        personRepository.delete(personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id)));
    }

    public Person update(int id, Person updatedPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setLastName(updatedPerson.getLastName());
        return personRepository.save(existingPerson);
    }

    public ClassroomDTO findClassroomForPerson(int personId) {
        return personRepository.findById(personId)
                .map(Person::getClassroom).map(classroomDTOMapper).orElse(null);
    }

    public void assignRoleToUser(int personId, Role role) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        person.setRole(role);
        personRepository.save(person);
    }

    public Settings findSettingsForPerson(int personId) {
        return personRepository.findById(personId)
                .map(Person::getSettings).orElse(null);
    }
}
