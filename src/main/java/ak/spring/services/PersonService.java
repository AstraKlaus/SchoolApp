package ak.spring.services;

import ak.spring.dto.AnswerDTO;
import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.dto.SettingsDTO;
import ak.spring.exceptions.ResourceNotFoundException;
import ak.spring.mappers.AnswerDTOMapper;
import ak.spring.mappers.ClassroomDTOMapper;
import ak.spring.mappers.PersonDTOMapper;
import ak.spring.mappers.SettingsDTOMapper;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.models.Settings;
import ak.spring.repositories.ClassroomRepository;
import ak.spring.repositories.PersonRepository;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final ClassroomRepository classroomRepository;
    private final PersonRepository personRepository;
    private final PersonDTOMapper personDTOMapper;
    private final SettingsDTOMapper settingsDTOMapper;
    private final AnswerDTOMapper answerDTOMapper;
    private final ClassroomDTOMapper classroomDTOMapper;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository,
                         ClassroomRepository classroomRepository, PersonRepository personRepository,
                         PersonDTOMapper personDTOMapper,
                         SettingsDTOMapper settingsDTOMapper, AnswerDTOMapper answerDTOMapper, ClassroomDTOMapper classroomDTOMapper) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.classroomRepository = classroomRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.settingsDTOMapper = settingsDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
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
                .orElseThrow(() -> new ResourceNotFoundException("Token", "id", token));
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

    public PersonDTO update(int id, PersonDTO updatedPerson) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));
        existingPerson.setUsername(updatedPerson.getUsername());
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setLastName(updatedPerson.getLastName());
        existingPerson.setPatronymic(updatedPerson.getPatronymic());
        existingPerson.setRole(updatedPerson.getRole());

        personRepository.save(existingPerson);
        return personDTOMapper.apply(existingPerson);
    }

    public ClassroomDTO findClassroomForPerson(int personId) {
        return personRepository.findById(personId)
                .map(person -> {
                    if (person.getClassroom() == null) {
                        throw new ResourceNotFoundException("Classroom", "personId", personId);
                    }
                    return classroomDTOMapper.apply(person.getClassroom());
                })
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
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
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
    }

    public void deleteSettingsForPerson(int personId) {
        personRepository.findById(personId).ifPresentOrElse(
                person -> {
                    person.setSettings(null);
                    personRepository.save(person);
                },
                () -> { throw new ResourceNotFoundException("Person", "id", personId); }
        );
    }


    public void deleteClassroomForPerson(int personId) {
        personRepository.findById(personId).ifPresentOrElse(
                person -> {
                    person.setClassroom(null);
                    personRepository.save(person);
                },
                () -> { throw new ResourceNotFoundException("Person", "id", personId); }
        );
    }


    public List<AnswerDTO> findAnswerForPerson(int personId) {
        return personRepository.findById(personId)
                .map(person -> person
                        .getAnswers()
                        .stream()
                        .map(answerDTOMapper)
                        .toList())
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
    }

    public PersonDTO updateClassroomForPerson(int personId, int classroomId) {
        return  personRepository.findById(personId)
                .map(person -> {
                    person.setClassroom(classroomRepository.findById(classroomId)
                            .orElseThrow(() -> new ResourceNotFoundException("Classroom", "id", classroomId)));
                    personRepository.save(person);
                    return personDTOMapper.apply(person);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
    }

    public Page<PersonDTO> findWithPagination(int page, int size) {
        Page<Person> people = personRepository.findAll(PageRequest.of(page, size, Sort.by("last_name").ascending()));
        return people.map(personDTOMapper);
    }

    public SettingsDTO updateSettingsForPerson(int personId, SettingsDTO updatedSettings) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        Settings settings = person.getSettings();
        if (settings == null) {
            settings = new Settings();
        }

        // Проверяем и обновляем связанные сущности
        settings.setTheme(updatedSettings.getTheme());
        settings.setIsSerif(updatedSettings.getIsSerif());
        settings.setFontSize(updatedSettings.getFontSize());
        settings.setLineHeight(updatedSettings.getLineHeight());
        settings.setLetterSpacing(updatedSettings.getLetterSpacing());
        settings.setImgHiding(updatedSettings.getImgHiding());

        person.setSettings(settings);
        personRepository.save(person);
        return settingsDTOMapper.apply(settings);
    }
}
