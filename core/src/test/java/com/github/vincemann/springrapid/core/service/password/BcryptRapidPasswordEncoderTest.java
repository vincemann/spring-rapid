package com.github.vincemann.springrapid.core.service.password;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BcryptRapidPasswordEncoderTest {

    BcryptRapidPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BcryptRapidPasswordEncoder();
    }


    @Test
    public void testValidPassword(){
        String pw = "whatAPasswordDude#57";
        String encoded = passwordEncoder.encode(pw);
        System.err.println(encoded);
        Assertions.assertTrue(passwordEncoder.matches(pw,encoded));
    }

    @Test
    public void testDoubleEncodingPassword(){
        String pw = "whatAPasswordDude#57";
        String encoded = passwordEncoder.encode(pw);
        System.err.println(encoded);
        String doubleEncoded = passwordEncoder.encode(encoded);
        System.err.println(doubleEncoded);
        Assertions.assertFalse(passwordEncoder.matches(pw,doubleEncoded));
    }
}