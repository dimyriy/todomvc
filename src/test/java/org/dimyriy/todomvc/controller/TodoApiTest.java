package org.dimyriy.todomvc.controller;

import com.google.gson.Gson;
import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.dimyriy.todomvc.controller.TodoControllerTest.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dimyriy
 * @date 22/02/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:todomvc-test.xml", "classpath:todomvc-web.xml"})
public class TodoApiTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TodoController todoController;
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("todoRepositoryMock")
    private TodoRepository repository;

    @Before
    public void setUp() {
        Mockito.reset(repository);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetAllReturnsAllItems() throws Exception {
        Todo first = COMPLETED_TODO;
        Todo second = UNCOMPLETED_TODO;
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(first, second));
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].title", is(first.getTitle())))
                .andExpect(jsonPath("$[0].completed", is(first.isCompleted())))

                .andExpect(jsonPath("$[1].title", is(second.getTitle())))
                .andExpect(jsonPath("$[1].completed", is(second.isCompleted())));

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testGetAllReturnsEmptyJsonArrayOnEmptyData() throws Exception {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testGetByIdExistingReturnsRightValue() throws Exception {
        Todo todo = COMPLETED_TODO;
        long id = todo.getId();
        Mockito.when(repository.findOne(id)).thenReturn(todo);
        expectCompletedTodo(mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isOk()));
        verify(repository, times(1)).findOne(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testGetByIdMissingIsNotFound() throws Exception {
        Todo todo = COMPLETED_TODO;
        long id = todo.getId();
        Mockito.when(repository.findOne(id)).thenReturn(null);
        mockMvc.perform(get("/api/todos/" + id))
                .andExpect(status().isNotFound());
        verify(repository, times(1)).findOne(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteByIdExistingIsOk() throws Exception {
        Mockito.when(repository.exists(UNCOMPLETED_TODO.getId())).thenReturn(true);
        mockMvc.perform(delete("/api/todos/" + UNCOMPLETED_TODO.getId()))
                .andExpect(status().isOk());
        verify(repository, times(1)).delete(UNCOMPLETED_TODO.getId());
        verify(repository, times(1)).exists(UNCOMPLETED_TODO.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteByIdMissingIsNotFound() throws Exception {
        Mockito.when(repository.exists(UNCOMPLETED_TODO.getId())).thenReturn(false);
        mockMvc.perform(delete("/api/todos/" + UNCOMPLETED_TODO.getId()))
                .andExpect(status().isNotFound());
        verify(repository, times(0)).delete(UNCOMPLETED_TODO.getId());
        verify(repository, times(1)).exists(UNCOMPLETED_TODO.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testDeleteCompletedIsOk() throws Exception {
        Mockito.when(repository.exists(UNCOMPLETED_TODO.getId())).thenReturn(true);
        mockMvc.perform(delete("/api/todos"))
                .andExpect(status().isOk());
        verify(repository, times(1)).deleteByCompleted(true);
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testCreateReturnsRightValue() throws Exception {
        Mockito.when(repository.save((Todo) anyObject())).thenReturn(UNCOMPLETED_TODO);
        String jsonContent = new Gson().toJson(NEW_TODO);
        expectUncompletedTodo(
                mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonContent))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        );
        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
        verify(repository, times(1)).save(argumentCaptor.capture());
        Todo value = argumentCaptor.getValue();
        assertThat(value.getTitle().equals(UNCOMPLETED_TODO.getTitle()));
        assertThat(value.isCompleted().equals(UNCOMPLETED_TODO.isCompleted()));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testUpdateExistingReturnsRightValue() throws Exception {
        Mockito.when(repository.findOne(UNCOMPLETED_TODO.getId())).thenReturn(UNCOMPLETED_TODO);
        Mockito.when(repository.save(UNCOMPLETED_TODO)).thenReturn(UNCOMPLETED_TODO);
        String jsonContent = new Gson().toJson(UNCOMPLETED_TODO);
        expectUncompletedTodo(mockMvc.perform(
                put("/api/todos/" + UNCOMPLETED_TODO.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonContent)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)));
        verify(repository, times(1)).findOne(UNCOMPLETED_TODO.getId());
        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);
        verify(repository, times(1)).save(argumentCaptor.capture());
        Todo value = argumentCaptor.getValue();
        assertThat(value.getTitle().equals(NEW_TODO.getTitle()));
        assertThat(value.isCompleted().equals(NEW_TODO.isCompleted()));
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void testUpdateMissingIsNotFound() throws Exception {
        Mockito.when(repository.findOne(UNCOMPLETED_TODO.getId())).thenReturn(null);
        Mockito.when(repository.save(UNCOMPLETED_TODO)).thenReturn(UNCOMPLETED_TODO);
        String jsonContent = new Gson().toJson(UNCOMPLETED_TODO);
        mockMvc.perform(
                put("/api/todos/" + UNCOMPLETED_TODO.getId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonContent))
                .andExpect(status().isNotFound());
        verify(repository, times(1)).findOne(UNCOMPLETED_TODO.getId());
        verify(repository, times(0)).save(ArgumentCaptor.forClass(Todo.class).capture());
        verifyNoMoreInteractions(repository);
    }

    private ResultActions expectCompletedTodo(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(jsonPath("$.title", is(COMPLETED_TODO.getTitle())))
                .andExpect(jsonPath("$.completed", is(COMPLETED_TODO.isCompleted())));
        return resultActions;
    }

    private ResultActions expectUncompletedTodo(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(jsonPath("$.title", is(UNCOMPLETED_TODO.getTitle())))
                .andExpect(jsonPath("$.completed", is(UNCOMPLETED_TODO.isCompleted())));
        return resultActions;
    }

}
