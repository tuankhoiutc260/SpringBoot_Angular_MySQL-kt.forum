package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PostService {
    PostResponse create(PostRequest postRequest);

    PostResponse findByID(String id);

    List<PostResponse> findAll();

    PostResponse update(String id, PostRequest postRequest);

    void deleteByID(String id);
}
