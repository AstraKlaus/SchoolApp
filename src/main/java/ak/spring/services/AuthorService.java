package ak.spring.services;

import ak.spring.controllers.SongRequest;
import ak.spring.models.Author;
import ak.spring.models.Song;
import ak.spring.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findByName(String name){
        return authorRepository.findByNameContainingIgnoreCase(name).orElse(null);

    }

    public Page<Author> findWithPagination(int offset, int pageSize){
        return authorRepository.findAll(PageRequest.of(offset, pageSize));
    }

    public Author findById(int id){
        return authorRepository.findById(id).orElse(null);
    }

    public Author findByUuid(UUID uuid){ return authorRepository.findByUuid(uuid).orElse(null);}

    public Author updateAuthor(int id, Author authorForUpdate){
        Author pastAuthor = findById(id);

        authorForUpdate.setId(id);
        authorForUpdate.setSongs(pastAuthor.getSongs());

        return authorRepository.save(authorForUpdate);
    }
    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }

    public Author uploadAuthor(Author author){
        Author newAuthor = Author.builder()
                .name(author.getName())
                .uuid(UUID.randomUUID())
                .build();
        return authorRepository.save(newAuthor);
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public List<Song> getSongs(Author author) {
        return author.getSongs();
    }
}
