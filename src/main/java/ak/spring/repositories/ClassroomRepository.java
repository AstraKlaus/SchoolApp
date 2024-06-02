package ak.spring.repositories;

import ak.spring.models.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    Optional<List<Classroom>> findByNameContainingIgnoreCase(String name);

}
