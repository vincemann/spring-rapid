package com.github.vincemann.springrapid.core.controller.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincemann.springrapid.core.util.JsonPatchUtil;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


class JsonDtoPropertyValidatorImplTest {

    JsonDtoPropertyValidatorImpl jsonDtoPropertyValidator;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jsonDtoPropertyValidator = new JsonDtoPropertyValidatorImpl();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    static class User implements Serializable{
        private String bday;

        public User() {
        }
    }

    static class TestEntity implements Serializable{

        private String name;
        private Set<User> users;
        private String secret;
        private User user;

        public TestEntity() {
        }
    }

    static class TestDto implements Serializable {
        private String name;
        private Set<Long> userIds;
        private Long userId;

        public TestDto() {
        }

        public TestDto(String name, Set<Long> userIds, Long userId) {
            this.name = name;
            this.userIds = userIds;
            this.userId = userId;
        }
    }

    static class ForbiddenTestDto implements Serializable {
        private String name;
        private Set<Long> userIds;
        private String secret;

        public ForbiddenTestDto(String name, Set<Long> userIds, String secret) {
            this.name = name;
            this.userIds = userIds;
            this.secret = secret;
        }

        public ForbiddenTestDto() {
        }
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
        String jsonPatch = JsonPatchUtil.createUpdateJsonRequest(
                JsonPatchUtil.createUpdateJsonLine("replace", "/name", "newName"),
                JsonPatchUtil.createUpdateJsonLine("add", "/userIds/-", "3"),
                JsonPatchUtil.createUpdateJsonLine("replace", "/userId/", "42")
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
        String jsonPatch = JsonPatchUtil.createUpdateJsonRequest(
                JsonPatchUtil.createUpdateJsonLine("replace", "/name", "newName"),
                JsonPatchUtil.createUpdateJsonLine("replace", "/secret", "newSecret"),
                JsonPatchUtil.createUpdateJsonLine("remove", "/userIds/-", "3")
        );
        Assertions.assertThrows(AccessDeniedException.class, () -> jsonDtoPropertyValidator.validatePatch(jsonPatch,TestDto.class));

    }


    @Test
    public void validDto() throws BadEntityException, AccessDeniedException, JsonProcessingException {
        TestDto testDto = new TestDto("newName",new HashSet<>(),6L);
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
        ForbiddenTestDto testDto = new ForbiddenTestDto("newName",new HashSet<>(),"secret");
        String jsonDto = objectMapper.writeValueAsString(testDto);
        Assertions.assertThrows(AccessDeniedException.class, () -> jsonDtoPropertyValidator.validateDto(jsonDto,TestDto.class));

    }
}