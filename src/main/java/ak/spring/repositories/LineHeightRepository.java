package ak.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ak.spring.models.LineHeight;

@Repository
public interface LineHeightRepository extends JpaRepository<LineHeight, Integer> {
}
