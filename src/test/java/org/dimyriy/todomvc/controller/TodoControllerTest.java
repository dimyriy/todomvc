package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dimyriy
 * @date 22/02/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:todomvc-test.xml", "classpath:todomvc-web.xml"})
public class TodoControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;
    @Autowired
    TodoController todoController;
    private MockMvc mockMvc;
    @Autowired
    private TodoRepository todoRepositoryMock;

    @Before
    public void setUp() {
        Mockito.reset(todoRepositoryMock);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void findAll() throws Exception {
        Todo first = new TodoBuilder().andId(1L).andTitle("Some title").andCompleted(false).build();
        Todo second = new TodoBuilder().andId(2L).andTitle("Some other title").andCompleted(true).build();
        Mockito.when(todoRepositoryMock.findAll()).thenReturn(Arrays.asList(first, second));
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Some title")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Some other title")))
                .andExpect(jsonPath("$[1].completed", is(true)));
        verify(todoRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(todoRepositoryMock);
    }

    @Test
    public void findOne() throws Exception {

    }

    @Test
    public void deleteCompleted() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void save() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    public static class TodoBuilder {
        private final Todo todo = new Todo();

        private TodoBuilder andId(long id) {
            todo.setId(id);
            return this;
        }

        private TodoBuilder andTitle(String title) {
            todo.setTitle(title);
            return this;
        }

        private TodoBuilder andCompleted(boolean completed) {
            todo.setCompleted(completed);
            return this;
        }

        private Todo build() {
            return todo;
        }
    }
}
