package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;

/**
 * @author dimyriy
 * @date 22/02/16
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration({"classpath:todomvc-test.xml", "classpath:todomvc-web.xml"})
public class TodoControllerTest {
    static final Comparator<Todo> todoComparator = (o1, o2) -> {
        if (o1.getTitle().equals(o2.getTitle()) && o1.isCompleted().equals(o2.isCompleted()))
            return 0;
        return -1;
    };
    private static final long COMPLETED_TODO_ID = 1;
    private static final long UNCOMPLETED_TODO_ID = 1;
    private static final String COMPLETED_TODO_TITLE = "Some title";
    static final Todo COMPLETED_TODO = new TodoBuilder().andId(COMPLETED_TODO_ID).andTitle(COMPLETED_TODO_TITLE).completed().build();
    private static final String UNCOMPLETED_TODO_TITLE = "Some other title";
    static final Todo UNCOMPLETED_TODO = new TodoBuilder().andId(UNCOMPLETED_TODO_ID).andTitle(UNCOMPLETED_TODO_TITLE).build();
    static final Todo NEW_TODO = new TodoBuilder().andTitle(UNCOMPLETED_TODO_TITLE).build();
    @InjectMocks
    TodoController todoController;

    @Mock
    TodoRepository repository;

    @Test
    public void testFindAllReturnsAllItems() {
        given(repository.findAll()).willReturn(Arrays.asList(COMPLETED_TODO, UNCOMPLETED_TODO));
        Iterable<Todo> result = when(todoController.findAll());
        then(result).usingFieldByFieldElementComparator().containsOnly(COMPLETED_TODO, UNCOMPLETED_TODO);
    }

    @Test
    public void testFindAllReturnsEmptyCollectionOnEmptyData() {
        given(repository.findAll()).willReturn(Collections.emptyList());
        Iterable<Todo> result = when(todoController.findAll());
        then(result).isEmpty();
    }

    @Test
    public void testFindOneReturnsNotFoundOnEmptyData() {
        given(repository.findOne(anyLong())).willReturn(null);
        when(todoController).findOne(anyLong());
        then(caughtException()).isInstanceOf(ResourceNotFoundException.class);
    }


    public static class TodoBuilder {
        private final Todo todo = new Todo();

        public TodoBuilder() {
            todo.setCompleted(false);
        }

        private TodoBuilder andId(long id) {
            todo.setId(id);
            return this;
        }

        private TodoBuilder andTitle(String title) {
            todo.setTitle(title);
            return this;
        }

        private TodoBuilder completed() {
            todo.setCompleted(true);
            return this;
        }

        private Todo build() {
            return todo;
        }
    }
}
