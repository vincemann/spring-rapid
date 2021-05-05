package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.RapidTestUtil;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


class RapidJsonDtoPropertyValidatorTest {

    RapidJsonDtoPropertyValidator jsonDtoPropertyValidator;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jsonDtoPropertyValidator = new RapidJsonDtoPropertyValidator();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @NoArgsConstructor
    static class User implements Serializable{
        private String bday;
    }

    @NoArgsConstructor
    static class TestEntity implements Serializable{
        private String name;
        private Set<User> users;
        private String secret;
        private User user;
    }



    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class TestDto implements Serializable {
        private String name;
        private Set<Long> userIds;
        private Long userId;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class ForbiddenTestDto implements Serializable {
        private String name;
        private Set<Long> userIds;
        private String secret;
    }

//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    static class InvalidTestDto implements Serializable {
//        private String name;
//        private Set<Long> userIds;
//        private String unknown;
//    }

    @Test
    public void validPatch() throws BadEntityException, AccessDeniedException, JsonProcessingException {
        String jsonPatch = RapidTestUtil.createUpdateJsonRequest(
                RapidTestUtil.createUpdateJsonLine("replace", "/name", "newName"),
                RapidTestUtil.createUpdateJsonLine("add", "/userIds/-", "3"),
                RapidTestUtil.createUpdateJsonLine("replace", "/userId/", "42")
        );
        Assertions.assertDoesNotThrow(() -> jsonDtoPropertyValidator.validatePatch(jsonPatch,TestDto.class));

    }

//    @Test
//    public void invalidPatch_unknownProperty() throws BadEntityException, AccessDeniedException, JsonProcessingException {
//        String jsonPatch = RapidTestUtil.createUpdateJsonRequest(
//                RapidTestUtil.createUpdateJsonLine("replace", "/unknown", "??"),
//                RapidTestUtil.createUpdateJsonLine("add", "/userIds/-", "3")
//        );
//        Assertions.assertThrows(BadEntityException.class, () -> jsonDtoPropertyValidator.validatePatch(jsonPatch,TestDto.class,TestEntity.class));
//
//    }

    @Test
    public void invalidPatch_forbiddenProperty() throws BadEntityException, AccessDeniedException, JsonProcessingException {
        String jsonPatch = RapidTestUtil.createUpdateJsonRequest(
                RapidTestUtil.createUpdateJsonLine("replace", "/name", "newName"),
                RapidTestUtil.createUpdateJsonLine("replace", "/secret", "newSecret"),
                RapidTestUtil.createUpdateJsonLine("remove", "/userIds/-", "3")
        );
        Assertions.assertThrows(AccessDeniedException.class, () -> jsonDtoPropertyValidator.validatePatch(jsonPatch,TestDto.class));

    }


    @Test
    public void validDto() throws BadEntityException, AccessDeniedException, JsonProcessingException {
        TestDto testDto = TestDto.builder()
                .name("newName")
                .userId(6L)
                .userIds(new HashSet<>())
                .build();
        String jsonDto = objectMapper.writeValueAsString(testDto);
        Assertions.assertDoesNotThrow(() -> jsonDtoPropertyValidator.validateDto(jsonDto,TestDto.class));

    }

//    @Test
//    public void invalidDto_unknownProperty() throws BadEntityException, AccessDeniedException, JsonProcessingException {
//        InvalidTestDto testDto = InvalidTestDto.builder()
//                .name("newName")
//                .userIds(new HashSet<>())
//                .unknown("??")
//                .build();
//        String jsonDto = objectMapper.writeValueAsString(testDto);
//        Assertions.assertThrows(BadEntityException.class, () -> jsonDtoPropertyValidator.validateDto(jsonDto,TestDto.class,TestEntity.class));
//
//    }

    @Test
    public void invalidDto_forbiddenProperty() throws BadEntityException, AccessDeniedException, JsonProcessingException {
        ForbiddenTestDto testDto = ForbiddenTestDto.builder()
                .name("newName")
                .userIds(new HashSet<>())
                .secret("secrettttt")
                .build();
        String jsonDto = objectMapper.writeValueAsString(testDto);
        Assertions.assertThrows(AccessDeniedException.class, () -> jsonDtoPropertyValidator.validateDto(jsonDto,TestDto.class));

    }
}