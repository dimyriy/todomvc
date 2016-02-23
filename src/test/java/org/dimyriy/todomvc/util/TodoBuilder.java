package org.dimyriy.todomvc.util;

import org.dimyriy.todomvc.model.Todo;

/**
 * @author dimyriy
 * @date 23/02/16
 */
public class TodoBuilder {
    private final Todo todo = new Todo();

    public TodoBuilder() {
        todo.setCompleted(false);
    }

    public static String toJsonExcludingId(Todo todo) {
        return JsonIdExcludedConverter.toJson(todo);
    }

    public TodoBuilder andId(long id) {
        todo.setId(id);
        return this;
    }

    public TodoBuilder andTitle(String title) {
        todo.setTitle(title);
        return this;
    }

    public TodoBuilder completed() {
        todo.setCompleted(true);
        return this;
    }

    public Todo build() {
        return todo;
    }
}
