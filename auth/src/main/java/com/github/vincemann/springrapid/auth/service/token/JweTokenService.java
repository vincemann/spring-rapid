package com.github.vincemann.springrapid.auth.service.token;

import org.springframework.stereotype.Component;

/**
 * encrypts and decrypts jwt's
 */
@Component
public interface JweTokenService extends JwtService {
}
