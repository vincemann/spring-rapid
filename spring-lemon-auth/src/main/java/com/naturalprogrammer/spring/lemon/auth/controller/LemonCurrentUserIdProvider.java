package com.naturalprogrammer.spring.lemon.auth.controller;

import com.naturalprogrammer.spring.lemon.auth.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.auth.domain.dto.user.LemonUserDto;
import com.naturalprogrammer.spring.lemon.auth.service.LemonService;
import com.naturalprogrammer.spring.lemon.auth.util.LecwUtils;
import io.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;


public class LemonCurrentUserIdProvider implements CurrentUserIdProvider {

    private LemonService<?,?,?> lemonService;

    public LemonCurrentUserIdProvider(LemonService<?, ?, ?> lemonService) {
        this.lemonService = lemonService;
    }

    @Override
    public String find() {
        LemonUserDto user = LecwUtils.currentUser();
        if (user==null)
            return null;
        AbstractUser<?> byEmail = lemonService.findByEmail(user.getEmail());
        if (byEmail==null){
            return null;
        }
        return byEmail.getId().toString();
    }
}
