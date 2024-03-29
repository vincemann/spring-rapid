package com.github.vincemann.springrapid.auth.sec;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;

public interface AuthenticatedPrincipalFactory {
    public RapidPrincipal create(AbstractUser<?> user);
}
