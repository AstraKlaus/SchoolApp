package ak.spring.repositories;

import ak.spring.models.Person;
import jakarta.transaction.Transactional;
import org.glassfish.grizzly.utils.ObjectPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String c);
}
