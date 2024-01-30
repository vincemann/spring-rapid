package com.github.vincemann.springrapid.core.model.audit;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Needed for auto-filling of the
 * {@link AuditingEntity} fields/columns
 *  
 */
@Slf4j
public abstract class AuthAuditorAwareImpl<ID extends Serializable>
			extends AbstractAuditorAware<ID> {

	private RapidSecurityContext securityContext;
	private IdConverter<ID> idConverter;

	public AuthAuditorAwareImpl() {

	}

	@Override
	protected ID currentId() {
		RapidPrincipal principal = securityContext.currentPrincipal();
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
	public void injectSecurityContext(RapidSecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	@Autowired
	public void injectIdIdConverter(IdConverter<ID> idIdConverter) {
		this.idConverter = idIdConverter;
	}
}
