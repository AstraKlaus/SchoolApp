package ak.spring.services;

import ak.spring.controllers.SongRequest;
import ak.spring.models.Author;
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
import java.util.UUID;

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

            Song song = songService.findById(id);

            if (!songs.contains(song)) {
                songs.add(songService.findById(id));
                person.setSongs(songs);

                personRepository.save(person);
            }
        }
    }

    public List<Song> findFavorites(String token){
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        return optionalToken.map(value -> value.getUser().getSongs()).orElse(null);
    }

    public Person findById(int id){
        return personRepository.findById(id).orElse(null);
    }

    public Person findByUuid(UUID uuid){ return personRepository.findByUuid(uuid).orElse(null);}

    public Person uploadPerson(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        return personRepository.save(person);
    }

    public void deletePerson(Person person){
        personRepository.delete(person);
    }

    public Person updatePerson(int id, Person person){
        Person pastPerson = findById(id);

        Person newPerson = Person.builder()
                .id(pastPerson.getId())
                .username(person.getUsername())
                .uuid(pastPerson.getUuid())
                .email(person.getEmail())
                .password(pastPerson.getPassword())
                .role(person.getRole())
                .build();

        return personRepository.save(newPerson);
    }

    public void deleteFavorites(int id, String token){
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isPresent()){
            Person person = findByToken(token);
            List<Song> songs = person.getSongs();
            Song song = songService.findById(id);

            if (songs.contains(song)) {
                System.out.println("nen");
                songs.remove(songService.findById(id));
                personRepository.save(person);
            }
        }
    }

    public boolean findFavorite(int id, UUID uuid) {
        Optional<Person> optionalPerson = personRepository.findByUuid(uuid);
        return optionalPerson.map(value -> value.getSongs().contains(songService.findById(id))).orElse(false);
    }
}
