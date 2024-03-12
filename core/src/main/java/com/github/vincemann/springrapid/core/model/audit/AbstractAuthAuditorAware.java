package com.github.vincemann.springrapid.core.model.audit;

import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.sec.RapidPrincipal;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * Needed for auto-filling of the
 * {@link AuditingEntity} fields/columns
 *  
 */
public abstract class AbstractAuthAuditorAware<ID extends Serializable>
			extends AbstractAuditorAware<ID> {

	private IdConverter<ID> idConverter;

	public AbstractAuthAuditorAware() {

	}

	@Override
	protected ID currentId() {
		RapidPrincipal principal = RapidSecurityContext.currentPrincipal();
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
	public void setIdIdConverter(IdConverter<ID> idIdConverter) {
		this.idConverter = idIdConverter;
	}
}
