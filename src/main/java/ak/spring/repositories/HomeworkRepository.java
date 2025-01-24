package ak.spring.repositories;

import ak.spring.models.Homework;
import ak.spring.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Integer>, AttachableRepository<Homework> {
    Optional<List<Homework>> findByNameContainingIgnoreCase(String name);
}
