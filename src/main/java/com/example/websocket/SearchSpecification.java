package com.example.websocket;

import com.example.websocket.entity.Post;
import com.example.websocket.entity.Post_;
import org.springframework.data.jpa.domain.Specification;

public class SearchSpecification {
    public static Specification<Post> allSearch(String keyword) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get(Post_.title), "%" + keyword + "%"),
                        criteriaBuilder.like(root.get(Post_.language), "%" + keyword + "%")
                )
        );
    }
}
