package ak.spring.controllers;

import ak.spring.models.Author;
import ak.spring.models.Person;
import ak.spring.models.Song;
import ak.spring.services.AdminService;
import ak.spring.services.PersonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins =  "http://localhost:8080")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping("/person")
    public Person createPerson(Person person){
        return personService.uploadPerson(person);
    }

    @PatchMapping("/person/{id}")
    public Person updatePerson(@PathVariable("id") String id, @RequestBody Person person){
        return personService.updatePerson(Integer.parseInt(id), person);
    }

    @DeleteMapping("/person/{id}")
    public void deletePerson(@PathVariable("id") String id){
        Person person = personService.findById(Integer.parseInt(id));
        if (person != null) personService.deletePerson(person);
    }

    @GetMapping("/person/{token}")
    public Person getPerson(@PathVariable("token") String token){
        return personService.findByToken(token);
    }
    @GetMapping("/personId/{uuid}")
    public Person getPersonByUuid(@PathVariable("uuid") UUID uuid){
        return personService.findByUuid(uuid);
    }
    @GetMapping("/personFavorites/{token}")
    public List<Song> getPersonFavorites(@PathVariable("token") String token){
        return personService.findFavorites(token);
    }

    @PostMapping("/personFavorites/{token}/{id}")
    public void addPersonFavorites(@PathVariable("id") String id,
                                   @PathVariable("token") String token){
        personService.addFavorites(Integer.parseInt(id), token);
    }

    @GetMapping("/person")
    public List<Person> getPeople(){
        return personService.findAll();
    }
}

