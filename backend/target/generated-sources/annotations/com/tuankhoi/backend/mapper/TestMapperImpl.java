package com.tuankhoi.backend.mapper;

import com.tuankhoi.backend.dto.response.TestResponse;
import com.tuankhoi.backend.entity.Test;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class TestMapperImpl implements TestMapper {

    @Override
    public TestResponse toResponse(Test test) {
        if ( test == null ) {
            return null;
        }

        TestResponse testResponse = new TestResponse();

        testResponse.setId( test.getId() );
        testResponse.setTitle( test.getTitle() );
        testResponse.setTestImage( test.getTestImage() );

        return testResponse;
    }
}
