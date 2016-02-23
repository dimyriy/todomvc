package org.dimyriy.todomvc.controller;

import org.dimyriy.todomvc.model.Todo;
import org.dimyriy.todomvc.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static org.dimyriy.todomvc.controller.TodoControllerTest.COMPLETED_TODO;
import static org.dimyriy.todomvc.controller.TodoControllerTest.UNCOMPLETED_TODO;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author dimyriy
 * @date 23/02/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:todomvc-persistence.xml", "classpath:todomvc-web.xml"})
@WebAppConfiguration
public class TodoControllerIntegrationTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TodoController todoController;
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void findAllReturnsRightCollection() throws Exception {
        Todo first = COMPLETED_TODO;
        Todo second = UNCOMPLETED_TODO;
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].title", is(first.getTitle())))
                .andExpect(jsonPath("$[0].completed", is(first.isCompleted())))

                .andExpect(jsonPath("$[1].title", is(second.getTitle())))
                .andExpect(jsonPath("$[1].completed", is(second.isCompleted())));
    }
}
