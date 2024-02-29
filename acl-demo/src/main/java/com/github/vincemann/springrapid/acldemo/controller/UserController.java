package com.github.vincemann.springrapid.acldemo.controller;

import com.github.vincemann.springrapid.acldemo.dto.user.MyFullUserDto;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.auth.controller.AbstractUserController;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.roles;

/**
 * controller exposing user operations, that are not specific to Vet or Owner but work for both
 *
 * {@link com.github.vincemann.springrapid.acldemo.service.user.SecuredDelegatingUserService} and
 * {@link com.github.vincemann.springrapid.acldemo.service.user.DelegatingUserService} are injected into services with
 * dependency on {@link UserService}.
 *
 *
 */
@Controller
public class UserController extends AbstractUserController<User, Long, UserService<User,Long>>  {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(roles(AuthRoles.ADMIN))
                .thenReturn(MyFullUserDto.class);

        super.configureDtoMappings(builder);
    }

    @Override
    public List<String> getIgnoredEndPoints() {
        return Lists.newArrayList(getSignupUrl(),getCreateUrl());
    }
}