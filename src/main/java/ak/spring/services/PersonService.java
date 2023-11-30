package ak.spring.services;

import ak.spring.models.Person;
import ak.spring.models.Song;
import ak.spring.repositories.PersonRepository;
import ak.spring.repositories.SongRepository;
import ak.spring.token.Token;
import ak.spring.token.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonService {

    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final PersonRepository personRepository;
    private final SongService songService;

    @Autowired
    public PersonService(PasswordEncoder passwordEncoder,
                         TokenRepository tokenRepository, PersonRepository personRepository, SongRepository songRepository, SongService songService) {
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.personRepository = personRepository;
        this.songService = songService;
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

    public Person findByToken(String token){
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        return optionalToken.map(Token::getUser).orElse(null);
    }

    public void addFavorites(int id, String token){
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isPresent()){
            Person person = findByToken(token);
            List<Song> songs = person.getSongs();

            songs.add(songService.findById(id));
            person.setSongs(songs);

            personRepository.save(person);
        }
    }

    public List<Song> findFavorites(String token){
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        return optionalToken.map(value -> value.getUser().getSongs()).orElse(null);
    }

    public Person findById(int id){
        return personRepository.findById(id).orElse(null);
    }


    public Person uploadPerson(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    public void deletePerson(Person person){
        personRepository.delete(person);
    }

    public void updatePerson(int id, Person person){
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            Person pastPerson = optionalPerson.get();

            person.setId(id);
            person.setUsername(pastPerson.getUsername());
            person.setRole(pastPerson.getRole());
            person.setEmail(pastPerson.getEmail());
            person.setSongs(pastPerson.getSongs());

            personRepository.save(person);
        }
    }
}
