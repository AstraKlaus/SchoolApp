package ak.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ak.spring.models.Settings;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {
}

