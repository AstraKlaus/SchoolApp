package ak.spring.repositories;

import ak.spring.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    Optional<List<Group>> findByNameContainingIgnoreCase(String name);

    Optional<Group> findByUuid(UUID uuid);
}
