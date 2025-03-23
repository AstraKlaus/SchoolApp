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
import ak.spring.models.*;
import ak.spring.repositories.*;
import ak.spring.requests.SettingsRequest;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    private final ThemeRepository themeRepository;
    private final FontSizeRepository fontSizeRepository;
    private final LineHeightRepository lineHeightRepository;
    private final LetterSpacingRepository letterSpacingRepository;
    private final SettingsRepository settingsRepository;
    private final ExcelService excelService;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository,
                         ClassroomRepository classroomRepository,
                         PersonRepository personRepository,
                         PersonDTOMapper personDTOMapper,
                         SettingsDTOMapper settingsDTOMapper,
                         AnswerDTOMapper answerDTOMapper,
                         ClassroomDTOMapper classroomDTOMapper,
                         ThemeRepository themeRepository,
                         FontSizeRepository fontSizeRepository,
                         LineHeightRepository lineHeightRepository,
                         LetterSpacingRepository letterSpacingRepository,
                         SettingsRepository settingsRepository,
                         ExcelService excelService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.classroomRepository = classroomRepository;
        this.personRepository = personRepository;
        this.personDTOMapper = personDTOMapper;
        this.settingsDTOMapper = settingsDTOMapper;
        this.answerDTOMapper = answerDTOMapper;
        this.classroomDTOMapper = classroomDTOMapper;
        this.themeRepository = themeRepository;
        this.fontSizeRepository = fontSizeRepository;
        this.lineHeightRepository = lineHeightRepository;
        this.letterSpacingRepository = letterSpacingRepository;
        this.settingsRepository = settingsRepository;
        this.excelService = excelService;
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
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", id));

        String username = person.getUsername();

        personRepository.delete(person);

        try {
            excelService.removeUserFromExcel(username);
        } catch (IOException e) {
            System.err.println("Ошибка при удалении пользователя из Excel: " + e.getMessage());
        }
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
        Page<Person> people = personRepository.findAll(PageRequest.of(page, size, Sort.by("lastName").ascending()));
        return people.map(personDTOMapper);
    }

    public SettingsDTO updateSettingsForPerson(int personId, SettingsRequest updatedSettings) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        Settings settings = Optional.ofNullable(person.getSettings())
                .orElseGet(Settings::new);

        // Получение существующих объектов из базы данных
        int themeId;
        switch (updatedSettings.getTheme()) {
            case "light" -> themeId = 1;
            case "dark" -> themeId = 2;
            case "blue" -> themeId = 3;
            case "default" -> themeId = 4;
            default -> throw new IllegalArgumentException("Недопустимое значение темы: " + updatedSettings.getTheme());
        }
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new ResourceNotFoundException("Theme", "id", themeId));
        settings.setTheme(theme);

        // Аналогично для других свойств
        int fontSizeId;
        switch (updatedSettings.getFontSize()) {
            case "normal" -> fontSizeId = 1;
            case "large" -> fontSizeId = 2;
            case "xlarge" -> fontSizeId = 3;
            default -> throw new IllegalArgumentException("Недопустимый размер шрифта: " + updatedSettings.getFontSize());
        }
        FontSize fontSize = fontSizeRepository.findById(fontSizeId)
                .orElseThrow(() -> new ResourceNotFoundException("FontSize", "id", fontSizeId));
        settings.setFontSize(fontSize);

        int lineHeightId;
        switch (updatedSettings.getLineHeight()) {
            case "normal" -> lineHeightId = 1;
            case "large" -> lineHeightId = 2;
            case "xlarge" -> lineHeightId = 3;
            default -> throw new IllegalArgumentException("Недопустимый межстрочный интервал: " + updatedSettings.getLineHeight());
        }
        LineHeight lineHeight = lineHeightRepository.findById(lineHeightId)
                .orElseThrow(() -> new ResourceNotFoundException("LineHeight", "id", lineHeightId));
        settings.setLineHeight(lineHeight);

        int letterSpacingId;
        switch (updatedSettings.getLetterSpacing()) {
            case "normal" -> letterSpacingId = 1;
            case "large" -> letterSpacingId = 2;
            case "xlarge" -> letterSpacingId = 3;
            default -> throw new IllegalArgumentException("Недопустимый межбуквенный интервал: " + updatedSettings.getLetterSpacing());
        }
        LetterSpacing letterSpacing = letterSpacingRepository.findById(letterSpacingId)
                .orElseThrow(() -> new ResourceNotFoundException("LetterSpacing", "id", letterSpacingId));
        settings.setLetterSpacing(letterSpacing);

        // Простые boolean значения
        settings.setIsSerif(updatedSettings.getIsSerif());
        settings.setImgHiding(updatedSettings.getImgHiding());

        // Сначала сохраняем Settings, если это новый объект
        if (settings.getId() == 0) {
            settings = settingsRepository.save(settings);
        }

        // Затем устанавливаем настройки для пользователя и сохраняем его
        person.setSettings(settings);
        personRepository.save(person);

        return settingsDTOMapper.apply(settings);
    }




}
