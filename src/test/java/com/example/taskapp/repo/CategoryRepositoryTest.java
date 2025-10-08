package com.example.taskapp.repo;

import com.example.taskapp.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest {

    @Autowired CategoryRepository repo;

    @Test
    void saveAndFindByName() {
        repo.save(Category.builder().name("Alpha").build());
        assertThat(repo.findByName("Alpha")).isPresent();
    }
}