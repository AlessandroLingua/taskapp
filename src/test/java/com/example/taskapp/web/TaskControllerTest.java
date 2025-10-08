package com.example.taskapp.web;

import com.example.taskapp.domain.Category;
import com.example.taskapp.repo.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class TaskControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired CategoryRepository catRepo;

    Long catId;

    @BeforeEach
    void setup() {
        catRepo.deleteAll();
        catId = catRepo.save(Category.builder().name("TestCat").build()).getId();
    }

    @Test
    void createAndListTask_withCategory_ok() throws Exception {
        var payload = """
      {"title":"T1","description":"D1","categoryId":%d}
      """.formatted(catId);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.categoryId", is(catId.intValue())))
                .andExpect(jsonPath("$.categoryName", is("TestCat")));

        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void validation_missingTitle_returns400() throws Exception {
        var bad = """
      {"title":"","description":"x","categoryId":%d}
      """.formatted(catId);

        mvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNotExisting_returns404() throws Exception {
        mvc.perform(get("/api/tasks/999999"))
                .andExpect(status().isNotFound());
    }
}