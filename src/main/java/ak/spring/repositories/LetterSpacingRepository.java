package ak.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ak.spring.models.LetterSpacing;

@Repository
public interface LetterSpacingRepository extends JpaRepository<LetterSpacing, Integer> {
}

