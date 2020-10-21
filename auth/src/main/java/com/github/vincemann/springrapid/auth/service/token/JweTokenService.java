package com.github.vincemann.springrapid.auth.service.token;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * encrypts and decrypts jwt's
 */
@ServiceComponent
public interface JweTokenService extends JwtService {
}
