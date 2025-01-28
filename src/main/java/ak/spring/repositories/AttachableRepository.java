package ak.spring.repositories;

import ak.spring.models.AttachableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface AttachableRepository<T extends AttachableEntity, ID>
        extends JpaRepository<T, ID> {
    Optional<T> findById(ID id);
}
