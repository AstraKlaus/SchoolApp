package ak.spring.services;

import ak.spring.models.Person;
import ak.spring.models.Song;
import ak.spring.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final SongService songService;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, SongService songService) {
        this.peopleRepository = peopleRepository;
        this.songService = songService;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void deleteSong(int id){
        Song song = songService.findOne(id);
        peopleRepository.findAll().forEach(person -> person.getSongs().remove(song));
    }

    @Transactional
    public void add(int id, Person owner) {
        getSongsByPersonId(owner.getId()).add(songService.findOne(id));
        save(findOne(owner.getId()));
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        updatedPerson.setId(id);
        updatedPerson.setPassword(encoder.encode(updatedPerson.getPassword()));
        if (updatedPerson.getRole() == null) updatedPerson.setRole(findOne(id).getRole());
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    public Optional<Person> getPersonByFullName(String fullName) {
        return peopleRepository.findByFullName(fullName);
    }

    public List<Song> getSongsByPersonId(int id) {
        Optional<Person> optionalPerson = peopleRepository.findById(id);
        if (optionalPerson.isPresent()){
            Person person = optionalPerson.get();

            return person.getSongs();
        }else { return Collections.emptyList();}
    }
}
