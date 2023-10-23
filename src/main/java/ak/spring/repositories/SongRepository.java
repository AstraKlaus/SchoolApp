package ak.spring.repositories;

import ak.spring.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

    Optional<Song> findByNameContainingIgnoreCase(String name);
}
