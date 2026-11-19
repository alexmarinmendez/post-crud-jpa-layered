package com.softer.post;

import com.softer.post.model.Post;
import com.softer.post.repository.TestH2Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    public void testAddPost() {
        Post post = new Post(1, 1, "Title 1", "Body 1", null);
        Post response = restTemplate.postForObject(baseUrl, post, Post.class);
        assertEquals("Title 1", response.getTitle());
        assertEquals(1, testH2Repository.findAll().size());
    }
}
