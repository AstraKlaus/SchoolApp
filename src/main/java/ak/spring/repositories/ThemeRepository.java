package ak.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ak.spring.models.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {
}
