package org.dimyriy.todomvc.repository;

import org.dimyriy.todomvc.model.Todo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author dimyriy
 * @date 18/02/16
 */
@RepositoryRestResource(collectionResourceRel = "todos", path = "todos")
public interface TodoRepository extends CrudRepository<Todo, Long> {
}