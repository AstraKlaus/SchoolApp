package ak.spring.repositories;

import ak.spring.models.Accord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccordRepository extends JpaRepository<Accord, Integer> {

    Optional<Accord> findByNameContainingIgnoreCase(String name);

    Optional<Accord> findByUuid(UUID uuid);
}
