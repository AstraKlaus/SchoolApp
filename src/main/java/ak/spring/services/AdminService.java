package ak.spring.services;

import ak.spring.models.Person;
import ak.spring.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    @Autowired
    public AdminService(PasswordEncoder passwordEncoder, PersonRepository personRepository) {
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    public List<Person> findAll() { return personRepository.findAll(); }

    public Person findOne(int id) {
        Optional<Person> foundPerson = personRepository.findById(id);
        return foundPerson.orElse(null);
    }

    public Person findByUsername(String name){
        Optional<Person> person = personRepository.findByUsername(name);
        return person.orElse(null);
    }

    public Person uploadPerson(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    public Person findById(int id){
        return personRepository.findById(id).orElse(null);
    }

    public void deletePerson(Person person){
        personRepository.delete(person);
    }

    public void updatePerson(int id, Person person){
        Person pastPerson = findById(id);

        person.setId(id);
        person.setUsername(pastPerson.getUsername());
        person.setRole(pastPerson.getRole());
        person.setEmail(pastPerson.getEmail());
        person.setSongs(pastPerson.getSongs());

        personRepository.save(person);
    }
}
