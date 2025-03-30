package ak.spring.repositories;

import ak.spring.models.Homework;
import ak.spring.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HomeworkRepository extends AttachableRepository<Homework, Integer> {
    Optional<List<Homework>> findByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(h) FROM Homework h " +
            "JOIN h.course c " +
            "JOIN c.curriculum cu " +
            "JOIN cu.classrooms cl " +
            "WHERE cl.id = :classroomId")
    long countByClassroomId(@Param("classroomId") int classroomId);

}
