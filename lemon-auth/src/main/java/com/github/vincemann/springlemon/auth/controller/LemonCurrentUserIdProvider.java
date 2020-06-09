package com.github.vincemann.springlemon.auth.controller;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import com.github.vincemann.springrapid.core.controller.rapid.CurrentUserIdProvider;


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
