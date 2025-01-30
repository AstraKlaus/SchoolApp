package ak.spring.repositories;

import ak.spring.models.Person;
import ak.spring.models.Course;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String c);

    @Query("SELECT COUNT(p) FROM Person p WHERE p.username LIKE :yearPattern")
    int getNextUserNumber(@Param("yearPattern") String yearPattern);

    List<Person> findByClassroomId(int classroomId);
}
