package ak.spring.repositories;

import ak.spring.models.Song;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface SongRepository extends JpaRepository<Song, Integer> {

    Optional<List<Song>> findByNameContainingIgnoreCase(String name);

    Optional<Song> findByUuid(UUID uuid);
}
