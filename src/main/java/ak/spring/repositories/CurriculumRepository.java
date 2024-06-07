package ak.spring.repositories;

import ak.spring.models.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {
    List<Curriculum> findByName(String name);
}

