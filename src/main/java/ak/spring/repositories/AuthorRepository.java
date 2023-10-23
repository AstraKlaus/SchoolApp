package ak.spring.repositories;

import ak.spring.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Optional<Author> findByNameContainingIgnoreCase(String name);
}
