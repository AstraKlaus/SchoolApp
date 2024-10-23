package ak.spring.mappers;

import ak.spring.dto.PersonDTO;
import ak.spring.dto.SettingsDTO;
import ak.spring.models.Person;
import ak.spring.models.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PersonDTOMapper implements Function<Person, PersonDTO> {

    private final SettingsDTOMapper settingsDTOMapper;

    @Autowired
    public PersonDTOMapper(SettingsDTOMapper settingsDTOMapper) {
        this.settingsDTOMapper = settingsDTOMapper;
    }

    @Override
    public PersonDTO apply(Person person) {
        Integer classroomId = (person.getClassroom() != null) ? person.getClassroom().getId() : null;
        SettingsDTO settings = (person.getSettings() != null) ? settingsDTOMapper.apply(person.getSettings()) : null;

        return PersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .patronymic(person.getPatronymic())
                .role(person.getRole())
                .settings(settings)
                .classroomId(classroomId)
                .build();
    }
}
