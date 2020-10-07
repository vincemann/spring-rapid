package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Needed for auto-filling of the
 * AbstractAuditable columns of AbstractUser
 *  
 * @author Sanjay Patel
 */
@Slf4j
public class LemonAuditorAware<ID extends Serializable>
			extends AbstractAuditorAware<ID> {

	private RapidSecurityContext<?> securityContext;
	private IdConverter<ID> idIdConverter;

	public LemonAuditorAware() {

	}

	@Override
	protected ID currentId() {
		RapidAuthenticatedPrincipal principal = securityContext.currentPrincipal();
		if (principal==null){
			return null;
		}
		String id = principal.getId();
		if (id==null){
			return null;
		}
		return idIdConverter.toId(id);
	}

	@Autowired
	public void injectSecurityContext(RapidSecurityContext<?> securityContext) {
		this.securityContext = securityContext;
	}

	@Autowired
	public void injectIdIdConverter(IdConverter<ID> idIdConverter) {
		this.idIdConverter = idIdConverter;
	}
}
