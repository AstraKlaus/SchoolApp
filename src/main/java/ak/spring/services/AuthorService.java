package ak.spring.services;

import ak.spring.models.Author;
import ak.spring.models.Song;
import ak.spring.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author findByName(String name){
        return authorRepository.findByNameContainingIgnoreCase(name).orElse(null);
    }

    public Author findById(int id){
        return authorRepository.findById(id).orElse(null);
    }

    public void updateAuthor(int id, Author authorForUpdate){
        Author pastAuthor = findById(id);

        authorForUpdate.setId(id);
        authorForUpdate.setSongs(pastAuthor.getSongs());

        authorRepository.save(authorForUpdate);
    }

    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }

    public Author uploadAuthor(Author author){
        return authorRepository.save(author);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public List<Song> getSongs(Author author) {
        return author.getSongs();
    }
}
