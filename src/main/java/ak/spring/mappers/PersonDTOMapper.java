package ak.spring.mappers;

import ak.spring.dto.PersonDTO;
import ak.spring.models.Course;
import ak.spring.models.Person;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PersonDTOMapper implements Function<Person, PersonDTO> {

    @Override
    public PersonDTO apply(Person person) {
        return new PersonDTO(person.getUsername(),
                person.getFirstName(),
                person.getLastName(),
                person.getRole(),
                person.getCourses().stream()
                        .map(Course::getName)
                        .toList(),
                person.getClassroom().getName());
    }
}
