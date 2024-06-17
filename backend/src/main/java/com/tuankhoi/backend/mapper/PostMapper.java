package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.PostDTO;
import com.tuankhoi.backend.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    //    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
    PostDTO mapToDTO(Post post);

    Post mapToEntity(PostDTO postDTO);

    @Mapping(target = "id", ignore = true) // Ignore id mapping for update
    void updatePostFromDTO(PostDTO postDTO, @MappingTarget Post post);
}
