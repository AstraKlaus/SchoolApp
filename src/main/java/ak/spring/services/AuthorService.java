package ak.spring.services;

import ak.spring.models.Author;
import ak.spring.repositories.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
