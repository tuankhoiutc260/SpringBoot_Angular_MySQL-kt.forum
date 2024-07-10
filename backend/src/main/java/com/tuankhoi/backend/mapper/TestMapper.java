package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.response.TestResponse;
import com.tuankhoi.backend.model.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

//@Mapper(componentModel = "spring")
@Mapper
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    TestResponse toResponse(Test test);
}