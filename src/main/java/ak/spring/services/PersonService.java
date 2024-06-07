package ak.spring.services;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.dto.SettingsDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.mappers.SettingsDTOMapper;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.repositories.PersonRepository;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final PersonRepository personRepository;
    private final PersonDTOMapper personDTOMapper;
    private final SettingsDTOMapper settingsDTOMapper;
    private final ClassroomDTOMapper classroomDTOMapper;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository,
                         PersonRepository personRepository,
                         PersonDTOMapper personDTOMapper,
                         SettingsDTOMapper settingsDTOMapper, ClassroomDTOMapper classroomDTOMapper) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.settingsDTOMapper = settingsDTOMapper;
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

    public PersonDTO uploadPerson(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
        return personDTOMapper.apply(person);
    }

    public void deletePerson(int id) {
        personRepository.delete(personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id)));
    }

    public PersonDTO update(int id, Person updatedPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setLastName(updatedPerson.getLastName());
        personRepository.save(existingPerson);
        return personDTOMapper.apply(existingPerson);
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

    public SettingsDTO findSettingsForPerson(int personId) {
        return personRepository.findById(personId)
                .map(Person::getSettings)
                .map(settingsDTOMapper)
                .orElse(null);
    }
}
