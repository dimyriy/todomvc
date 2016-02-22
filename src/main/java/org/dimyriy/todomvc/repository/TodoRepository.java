package org.dimyriy.todomvc.repository;

import org.dimyriy.todomvc.model.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author dimyriy
 * @date 18/02/16
 */
@RepositoryRestResource(path = "todoshal", collectionResourceRel = "todos")
public interface TodoRepository extends CrudRepository<Todo, Long> {
    void deleteByCompleted(boolean completed);
}