package ak.spring.repositories;

import ak.spring.models.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {
    List<Curriculum> findByName(String name);
}

