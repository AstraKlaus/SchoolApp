package ak.spring.repositories;

import ak.spring.models.AttachableEntity;

import java.util.Optional;

public interface AttachableRepository<T extends AttachableEntity> {
    Optional<T> findById(int id);
    T save(T entity);
}
