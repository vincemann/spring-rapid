package com.github.vincemann.springrapid.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface RapidPasswordEncoder extends PasswordEncoder {

    public boolean isEncoded(String password);
}
