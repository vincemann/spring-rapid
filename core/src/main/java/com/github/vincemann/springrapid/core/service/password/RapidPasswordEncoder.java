package com.github.vincemann.springrapid.core.service.password;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface RapidPasswordEncoder extends PasswordEncoder {

    public boolean isEncrypted(String password);
}
