package com.softer.post;

import com.softer.post.model.Post;
import com.softer.post.repository.TestH2Repository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ApplicationTest {
    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository testH2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port+"").concat("/api/posts");
    }

    // --------------------------------- Integration Tests ----------------------------------
    @Test
    //@Order(1)
    @DisplayName("should register a new post")
    @Sql(statements = "DELETE FROM post", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test3_testAddPost() {
        Post post = new Post(1, 1, "Title 1", "Body 1", null);
        Post response = restTemplate.postForObject(baseUrl, post, Post.class);
        assertEquals("Title 1", response.getTitle());
        assertEquals(1, testH2Repository.findAll().size());
    }

    @Test
    //@Order(2)
    @DisplayName("should get 0 posts when table is empty")
    public void test1_testGetPostsWhenTableIsEmpty() {
        List<Post> response = restTemplate.getForObject(baseUrl, List.class);
        assertEquals(0, response.size());
        assertEquals(0, testH2Repository.findAll().size());
    }

    @Test
    //@Order(3)
    @DisplayName("should get all posts when table is not empty")
    @Sql(statements = "INSERT INTO post(id, user_id, title, body, version) VALUES (1, 1, 'Title 1', 'Body 1', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM post", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test2_testGetPostsWhenTableIsNotEmpty() {
        List<Post> response = restTemplate.getForObject(baseUrl, List.class);
        assertEquals(1, response.size());
        assertEquals(1, testH2Repository.findAll().size());
    }

    @Test
    @DisplayName("should get post with id=1")
    @Sql(statements = "INSERT INTO post(id, user_id, title, body, version) VALUES (1, 1, 'Title 1', 'Body 1', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM post", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test4_FindPostById() {
        Post post = restTemplate.getForObject(baseUrl + "/{id}", Post.class, 1);
        assertAll(
                () -> assertNotNull(post),
                () -> assertEquals(1, post.getId()),
                () -> assertEquals("Title 1", post.getTitle())
        );
    }

    @Test
    @DisplayName("should update post with id=1")
    @Sql(statements = "INSERT INTO post(id, user_id, title, body, version) VALUES (1, 1, 'Title 1', 'Body 1', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM post", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test5_testUpdatePost() {
        Post post = new Post(1, 1, "New Title 1", "Body 1", 1);
        restTemplate.put(baseUrl+"/{id}", post, 1);
        Post postFromDB = testH2Repository.findById(1).get();
        assertAll(
                () -> assertNotNull(postFromDB),
                () -> assertEquals("New Title 1", postFromDB.getTitle())
        );
    }

    @Test
    @DisplayName("should delete post with id=1")
    @Sql(statements = "INSERT INTO post(id, user_id, title, body, version) VALUES (1, 1, 'Title 1', 'Body 1', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM post", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void test6_testDeletePost() {
        int recordCount = testH2Repository.findAll().size();
        assertEquals(1, recordCount);
        restTemplate.delete(baseUrl+"/{id}", 1);
        assertEquals(0, testH2Repository.findAll().size());
    }
}
