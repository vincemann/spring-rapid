package com.github.vincemann.springrapid.core.service.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

public class BcryptRapidPasswordEncoder extends BCryptPasswordEncoder implements RapidPasswordEncoder {
    private Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    @Override
    public boolean isEncrypted(String password) {
        return BCRYPT_PATTERN.matcher(password).matches();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return super.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return super.matches(rawPassword,encodedPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return super.upgradeEncoding(encodedPassword);
    }
}
