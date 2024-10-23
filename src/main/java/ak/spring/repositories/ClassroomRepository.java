package ak.spring.repositories;

import ak.spring.models.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    Optional<List<Classroom>> findByNameContainingIgnoreCase(String name);

}
