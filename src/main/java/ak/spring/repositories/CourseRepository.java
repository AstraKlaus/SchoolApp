package ak.spring.repositories;

import ak.spring.models.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Optional<List<Course>> findByNameContainingIgnoreCase(String name);

}
