package ak.spring.controllers;

import ak.spring.models.Course;
import ak.spring.models.Person;
import ak.spring.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins =  "http://localhost:8080")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> findAll() {
        List<Person> persons = personService.findAll();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<Person> findByUsername(@PathVariable String username) {
        Person person = personService.findByUsername(username)
                .orElse(null);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Person> findByToken(@PathVariable String token) {
        Person person = personService.findByToken(token);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Person person = personService.findById(id);
        return ResponseEntity.ok(person);
    }

    @PostMapping
    public ResponseEntity<Person> uploadPerson(@Valid @RequestBody Person person) {
        Person savedPerson = personService.uploadPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable int id) {
        Person person = personService.findById(id);
        personService.deletePerson(person);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable int id, @Valid @RequestBody Person updatedPerson) {
        Person person = personService.update(id, updatedPerson);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{personId}/courses")
    public ResponseEntity<List<Course>> findCoursesForPerson(@PathVariable int personId) {
        List<Course> courses = personService.findCoursesForPerson(personId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{personId}/enroll/{courseId}")
    public ResponseEntity<Void> enrollPersonInCourse(@PathVariable int personId, @PathVariable int courseId) {
        personService.enrollPersonInCourse(personId, courseId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{personId}/unenroll/{courseId}")
    public ResponseEntity<Void> removePersonFromCourse(@PathVariable int personId, @PathVariable int courseId) {
        personService.removePersonFromCourse(personId, courseId);
        return ResponseEntity.noContent().build();
    }
}