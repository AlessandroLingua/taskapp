package com.example.taskapp.web;

import com.example.taskapp.domain.Category;
import com.example.taskapp.repo.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerPagingTest {

    @Autowired MockMvc mvc;
    @Autowired CategoryRepository catRepo;

    Long catId;

    @BeforeEach
    void setup() throws Exception {
        catRepo.deleteAll();
        var cat = catRepo.save(Category.builder().name("CatA").build());
        catId = cat.getId();

        for (int i=1; i<=3; i++) {
            var payload = """
        {"title":"T%s","description":"D","categoryId":%d}
        """.formatted(i, catId);
            mvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    void pagedAndSortedAndFiltered() throws Exception {
        mvc.perform(get("/api/tasks/paged")
                        .param("page","0").param("size","2").param("sort","title,desc")
                        .param("categoryId", String.valueOf(catId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }
}