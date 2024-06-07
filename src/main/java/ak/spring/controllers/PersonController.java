package ak.spring.controllers;

import ak.spring.dto.ClassroomDTO;
import ak.spring.dto.PersonDTO;
import ak.spring.dto.SettingsDTO;
import ak.spring.models.Person;
import ak.spring.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/api/people")
@CrossOrigin(origins =  "http://localhost:5173")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<PersonDTO>> findAll() {
        List<PersonDTO> persons = personService.findAll();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<PersonDTO> findByUsername(@PathVariable String username) {
        PersonDTO person = personService.findByUsername(username);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Person> findByToken(@PathVariable String token) {
        Person person = personService.findByToken(token);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> findById(@PathVariable int id) {
        PersonDTO person = personService.findById(id);
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<PersonDTO> uploadPerson(@Valid @RequestBody Person person) {
        PersonDTO savedPerson = personService.uploadPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> update(@PathVariable int id,
                                         @Valid @RequestBody Person updatedPerson) {
        PersonDTO person = personService.update(id, updatedPerson);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{personId}/classroom")
    public ResponseEntity<ClassroomDTO> findClassroomForPerson(@PathVariable int personId) {
        ClassroomDTO classroom = personService.findClassroomForPerson(personId);
        return ResponseEntity.ok(classroom);
    }

    @GetMapping("/{personId}/settings")
    public ResponseEntity<SettingsDTO> findSettingsForPerson(@PathVariable int personId) {
        SettingsDTO settings = personService.findSettingsForPerson(personId);
        return ResponseEntity.ok(settings);
    }
}