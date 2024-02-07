package com.github.vincemann.springrapid.auth.controller;

import com.github.vincemann.springrapid.auth.controller.dto.SignupDto;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

public class UserController<SignupForm extends SignupDto> {

    public ResponseEntity<String> signup(@Valid SignupDto signupDto, HttpServletRequest request, HttpServletResponse response) throws BadEntityException, IOException, EntityNotFoundException, AlreadyRegisteredException {


        String jsonDto = readBody(request);
        Class<?> dtoClass = createDtoClass(getSignupUrl(), Direction.REQUEST,request,null);
        SignupDto signupDto = (SignupDto) getJsonMapper().readDto(jsonDto, dtoClass);
        getDtoValidationStrategy().validate(signupDto);
        U saved = signupService.signup(signupDto);

        U user = getDtoMapper().mapToEntity(signupDto, getEntityClass());

        appendFreshToken(saved,response);
        Object dto = getDtoMapper().mapToDto(saved,
                createDtoClass(getSignupUrl(), Direction.RESPONSE,request, saved));
        return ok(getJsonMapper().writeDto(dto));
    }
}
