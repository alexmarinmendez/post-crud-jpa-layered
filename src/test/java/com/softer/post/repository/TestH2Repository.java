package com.softer.post.repository;

import com.softer.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<Post, Integer> {
}
