package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static com.googlecode.catchexception.apis.BDDCatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author dimyriy
 * @date 22/02/16
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration({"classpath:todomvc-test.xml", "classpath:todomvc-web.xml"})
public class TodoControllerTest {
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
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testFindAllReturnsEmptyCollectionOnEmptyData() {
        given(repository.findAll()).willReturn(Collections.emptyList());
        Iterable<Todo> result = when(todoController.findAll());
        then(result).isEmpty();
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testFindOneReturnsNotFoundOnEmptyData() {
        given(repository.findOne(anyLong())).willReturn(null);
        when(todoController).findOne(COMPLETED_TODO_ID);
        then(caughtException()).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, times(1)).findOne(COMPLETED_TODO_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testFindOneReturnsRightValueOnExistingData() {
        given(repository.findOne(COMPLETED_TODO_ID)).willReturn(COMPLETED_TODO);
        Todo result = when(todoController).findOne(COMPLETED_TODO_ID);
        then(result).isEqualToComparingFieldByField(result);
        verify(repository, times(1)).findOne(COMPLETED_TODO_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteExistingDataCompletesNormally() {
        given(repository.exists(COMPLETED_TODO_ID)).willReturn(true);
        when(todoController).delete(COMPLETED_TODO_ID);
        verify(repository, times(1)).delete(COMPLETED_TODO_ID);
        verify(repository, times(1)).exists(COMPLETED_TODO_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteNonExisting() {
        given(repository.exists(COMPLETED_TODO_ID)).willReturn(false);
        when(todoController).delete(COMPLETED_TODO_ID);
        then(caughtException()).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, times(0)).delete(COMPLETED_TODO_ID);
        verify(repository, times(1)).exists(COMPLETED_TODO_ID);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteCompleted() {
        when(todoController).deleteCompleted();
        verify(repository, times(1)).deleteByCompleted(true);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testPost() {
        given(repository.save(NEW_TODO)).willReturn(UNCOMPLETED_TODO);
        Todo result = when(todoController).save(NEW_TODO);
        then(result).isEqualToComparingFieldByField(UNCOMPLETED_TODO);
        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
        verify(repository, times(1)).save(argumentCaptor.capture());
        Todo value = argumentCaptor.getValue();
        assertThat(value.getTitle().equals(UNCOMPLETED_TODO.getTitle()));
        assertThat(value.isCompleted().equals(UNCOMPLETED_TODO.isCompleted()));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testPut() {
        given(repository.findOne(COMPLETED_TODO_ID)).willReturn(COMPLETED_TODO);
        given(repository.save(COMPLETED_TODO)).willReturn(COMPLETED_TODO);
        Todo result = when(todoController).update(COMPLETED_TODO_ID, COMPLETED_TODO);
        then(result).isEqualToComparingFieldByField(COMPLETED_TODO);
        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
        verify(repository, times(1)).findOne(COMPLETED_TODO_ID);
        verify(repository, times(1)).save(argumentCaptor.capture());
        Todo value = argumentCaptor.getValue();
        assertThat(value.getTitle().equals(COMPLETED_TODO.getTitle()));
        assertThat(value.isCompleted().equals(COMPLETED_TODO.isCompleted()));
        assertThat(value.getId()).isEqualTo(COMPLETED_TODO_ID);
        verifyNoMoreInteractions(repository);
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
