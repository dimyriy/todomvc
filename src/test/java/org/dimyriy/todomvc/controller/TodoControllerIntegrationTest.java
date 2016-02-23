package org.dimyriy.todomvc.controller;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.util.TodoBuilder;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.dimyriy.todomvc.controller.TodoControllerTest.*;
import static org.dimyriy.todomvc.util.TodoBuilder.toJsonExcludingId;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dimyriy
 * @date 23/02/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:todomvc-persistence-test.xml", "classpath:todomvc-web.xml", "classpath:todomvc-test.xml"})
@WebAppConfiguration
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
public class TodoControllerIntegrationTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TodoController todoController;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @DatabaseSetup("/db/todoDataSet.xml")
    @ExpectedDatabase("/db/todoDataSet.xml")
    @Test
    public void testGetAllReturnsAllItems() throws Exception {
        Todo first = UNCOMPLETED_TODO;
        Todo second = COMPLETED_TODO;
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id", is(-2)))
                .andExpect(jsonPath("$[0].title", is(first.getTitle())))
                .andExpect(jsonPath("$[0].completed", is(first.isCompleted())))

                .andExpect(jsonPath("$[1].id", is(-1)))
                .andExpect(jsonPath("$[1].title", is(second.getTitle())))
                .andExpect(jsonPath("$[1].completed", is(second.isCompleted())));
    }

    @DatabaseSetup("/db/todoEmptyDataSet.xml")
    @ExpectedDatabase("/db/todoEmptyDataSet.xml")
    @Test
    public void testGetAllReturnsEmptyJsonArrayOnEmptyDataSet() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DatabaseSetup("/db/todoDataCreatedDataSet.xml")
    @ExpectedDatabase("/db/todoDataCreatedDataSet.xml")
    @Test
    public void testGetByIdExistingReturnsRightValue() throws Exception {
        Todo todo = UNCOMPLETED_TODO;
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(todo.isCompleted())))
                .andExpect(jsonPath("$.title", is(todo.getTitle())));
    }

    @DatabaseSetup("/db/todoDataCreatedDataSet.xml")
    @ExpectedDatabase("/db/todoDataCreatedDataSet.xml")
    @Test
    public void testGetByIdMissingIsNotFound() throws Exception {
        mockMvc.perform(get("/api/todos/1000"))
                .andExpect(status().isNotFound());
    }

    @DatabaseSetup("/db/todoDataSet.xml")
    @ExpectedDatabase("/db/todoDataCreatedDataSet.xml")
    @Test
    public void testCreateReturnsRightValue() throws Exception {
        Todo todo = NEW_TODO;
        String jsonContent = toJsonExcludingId(todo);
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(todo.isCompleted())))
                .andExpect(jsonPath("$.title", is(todo.getTitle())));
    }

    @DatabaseSetup("/db/todoDataManyCompletedDataSet.xml")
    @ExpectedDatabase("/db/todoDataDeleteCompletedDataSet.xml")
    @Test
    public void testDeleteCompletedIsOk() throws Exception {
        mockMvc.perform(delete("/api/todos"))
                .andExpect(status().isOk());
    }

    @DatabaseSetup("/db/todoDataCreatedDataSet.xml")
    @ExpectedDatabase("/db/todoDataSet.xml")
    @Test
    public void testDeleteByIdExistingIsOk() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isOk());
    }

    @DatabaseSetup("/db/todoOneElementDataSet.xml")
    @ExpectedDatabase("/db/todoEmptyDataSet.xml")
    @Test
    public void testDeleteLastElementByIdIsOk() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isOk());
    }

    @DatabaseSetup("/db/todoOneElementDataSet.xml")
    @ExpectedDatabase("/db/todoEmptyDataSet.xml")
    @Test
    public void testDeleteCompletedWhenDataContainsOnlyCompletedIsOk() throws Exception {
        mockMvc.perform(delete("/api/todos"))
                .andExpect(status().isOk());
    }

    @DatabaseSetup("/db/todoDataSet.xml")
    @ExpectedDatabase("/db/todoDataSet.xml")
    @Test
    public void testDeleteByIdMissingIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNotFound());
    }

    @DatabaseSetup("/db/todoDataCreatedDataSet.xml")
    @ExpectedDatabase("/db/todoDataUpdatedDataSet.xml")
    @Test
    public void testUpdateExistingReturnsRightValue() throws Exception {
        Todo todo = new TodoBuilder().andTitle("Some other title updated").completed().build();
        String jsonContent = TodoBuilder.toJsonExcludingId(todo);
        mockMvc.perform(
                put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(todo.isCompleted())))
                .andExpect(jsonPath("$.title", is(todo.getTitle())));
    }

    @DatabaseSetup("/db/todoDataCreatedDataSet.xml")
    @ExpectedDatabase("/db/todoDataCreatedDataSet.xml")
    @Test
    public void testUpdateMissingReturnsNotFound() throws Exception {
        String jsonContent = TodoBuilder.toJsonExcludingId(UNCOMPLETED_TODO);
        mockMvc.perform(
                put("/api/todos/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonContent))
                .andExpect(status().isNotFound());
    }
}
