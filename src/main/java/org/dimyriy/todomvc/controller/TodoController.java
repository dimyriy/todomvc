package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.composed.web.Delete;
import org.springframework.composed.web.Get;
import org.springframework.composed.web.Post;
import org.springframework.composed.web.Put;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dimyriy
 * @date 19/02/16
 */
@RestController
@RequestMapping(value = "/api/todos")
public class TodoController {
    @Autowired
    private TodoRepository todoRepository;

    @Get
    public Iterable<Todo> getAll() {
        return todoRepository.findAll();
    }

    @Get("{id}")
    public Todo getById(@PathVariable long id) {
        Todo todo = todoRepository.findOne(id);
        if (todo == null)
            throw new ResourceNotFoundException();
        return todo;
    }

    @Transactional
    @Delete("{id}")
    public void delete(@PathVariable long id) {
        if (!todoRepository.exists(id))
            throw new ResourceNotFoundException();
        todoRepository.delete(id);
    }

    @Transactional
    @Delete
    public void deleteCompleted() {
        todoRepository.deleteByCompleted(true);
    }

    @Transactional
    @Post
    public Todo create(@RequestBody Todo entity) {
        return todoRepository.save(entity);
    }

    @Transactional
    @Put("{id}")
    public Todo update(@PathVariable long id, @RequestBody Todo entity) {
        Todo oldEntity = getById(id);
        if (entity.getTitle() != null)
            oldEntity.setTitle(entity.getTitle());
        if (entity.isCompleted() != null) {
            oldEntity.setCompleted(entity.isCompleted());
        }
        return todoRepository.save(oldEntity);
    }
}
