package ak.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ak.spring.models.FontSize;

@Repository
public interface FontSizeRepository extends JpaRepository<FontSize, Integer> {
}

