package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @author dimyriy
 * @date 19/02/16
 */
@RestController
@RequestMapping(value = "/api/todos")
public class TodoController {
    @Autowired
    private TodoRepository repository;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Iterable<Todo> findAll() {
        return repository.findAll();
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public Todo findOne(@PathVariable("id") long id) {
        return repository.findOne(id);
    }

    @Transactional
    @RequestMapping(path = "", method = RequestMethod.DELETE)
    public void deleteCompleted() {
        repository.deleteByCompleted(true);
    }

    @Transactional
    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") long id) {
        repository.delete(id);
    }

    @Transactional
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Todo save(@RequestBody Todo entity) {
        return repository.save(entity);
    }

    @Transactional
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Todo update(@PathVariable("id") long id, @RequestBody Todo entity) {
        Todo oldEntity = findOne(id);
        if (entity.getTitle() != null)
            oldEntity.setTitle(entity.getTitle());
        if (entity.isCompleted() != null) {
            oldEntity.setCompleted(entity.isCompleted());
        }
        return repository.save(oldEntity);
    }
}
