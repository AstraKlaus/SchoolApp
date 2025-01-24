package ak.spring.repositories;
import ak.spring.models.AttachableEntity;
import ak.spring.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer>, AttachableRepository<Lesson> {

    Optional<List<Lesson>> findByNameContainingIgnoreCase(String name);

}
