package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.request.PostRequest;
import com.tuankhoi.backend.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse create(final PostRequest postRequest);

    PostResponse findByID(final String id);

    List<PostResponse> findAll();

    PostResponse update(final String id, final PostRequest postRequest);

    void deleteByID(final String id);
}
