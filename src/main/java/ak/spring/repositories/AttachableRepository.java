package ak.spring.repositories;

import ak.spring.models.AttachableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachableRepository<T extends AttachableEntity, ID>
        extends JpaRepository<T, ID> {
    Optional<T> findById(ID id);
}
