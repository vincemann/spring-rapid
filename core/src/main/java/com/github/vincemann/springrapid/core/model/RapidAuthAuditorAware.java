package com.github.vincemann.springrapid.core.model;

import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.security.RapidAuthenticatedPrincipal;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Needed for auto-filling of the
 * {@link AuditingEntity} fields/columns
 *  
 */
@Slf4j
public abstract class RapidAuthAuditorAware<ID extends Serializable>
			extends AbstractAuditorAware<ID> {

	private RapidSecurityContext<?> securityContext;
	private IdConverter<ID> idConverter;

	public RapidAuthAuditorAware() {

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
		return idConverter.toId(id);
	}

	@Autowired
	public void injectSecurityContext(RapidSecurityContext<?> securityContext) {
		this.securityContext = securityContext;
	}

	@Autowired
	public void injectIdIdConverter(IdConverter<ID> idIdConverter) {
		this.idConverter = idIdConverter;
	}
}
