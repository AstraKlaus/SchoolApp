package ak.spring.mappers;

import ak.spring.dto.PersonDTO;
import ak.spring.models.Person;
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
        return PersonDTO.builder()
                .id(person.getId())
                .username(person.getUsername())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .role(person.getRole())
                .settings(settingsDTOMapper.apply(person.getSettings()))
                .classroomName(person.getClassroom().getName())
                .build();
    }
}
