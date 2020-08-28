package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.AbstractRapidSecurityContext;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class LemonSecurityContextImpl extends AbstractRapidSecurityContext<LemonAuthenticatedPrincipal> {
}
