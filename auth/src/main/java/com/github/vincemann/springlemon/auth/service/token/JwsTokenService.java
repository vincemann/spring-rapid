package com.github.vincemann.springlemon.auth.service.token;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;

/**
 * signs jwt's and verifies jwt's signatures
 */
@ServiceComponent
public interface JwsTokenService extends JwtService {
}
