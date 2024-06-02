package ak.spring.controllers;

import ak.spring.dto.PersonDTO;
import ak.spring.models.Person;
import ak.spring.models.Role;
import ak.spring.services.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    private final PersonService personService;

    @Autowired
    public AdminController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        List<PersonDTO> users = personService.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users")
    public ResponseEntity<Person> createUser(@Valid @RequestBody Person person) {
        Person savedPerson = personService.uploadPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Person> updateUser(@PathVariable int id, @Valid @RequestBody Person updatedPerson) {
        Person person = personService.update(id, updatedPerson);
        return ResponseEntity.ok(person);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable int userId, @RequestBody Role role) {
        personService.assignRoleToUser(userId, role);
        return ResponseEntity.ok().build();
    }

}