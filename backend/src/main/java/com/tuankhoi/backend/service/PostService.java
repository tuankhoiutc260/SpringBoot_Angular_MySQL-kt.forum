package com.tuankhoi.backend.service;

import com.tuankhoi.backend.dto.PostDTO;

import java.util.List;
import java.util.UUID;

public interface PostService {
    List<PostDTO> findAll();

    PostDTO findByID(final UUID id);

    PostDTO create(final PostDTO postDTO);

    PostDTO update(final UUID id, final PostDTO postDTO);

    void deleteByID(final UUID id);
}
