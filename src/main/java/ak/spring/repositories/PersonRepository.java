package ak.spring.repositories;

import ak.spring.models.Person;
import org.glassfish.grizzly.utils.ObjectPool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
}
