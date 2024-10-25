package com.example.websocket.dao;

import com.example.websocket.dto.PostDTO;
import com.example.websocket.entity.Member;
import com.example.websocket.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByCategory(String category,Pageable pageable);
}
