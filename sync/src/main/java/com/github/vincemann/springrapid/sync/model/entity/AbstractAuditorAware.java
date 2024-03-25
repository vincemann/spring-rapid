package com.github.vincemann.springrapid.sync.model.entity;

import org.springframework.data.domain.AuditorAware;

import java.io.Serializable;
import java.util.Optional;


public abstract class AbstractAuditorAware<ID extends Serializable>
				implements AuditorAware<ID> {

	protected abstract ID currentId();
	
	@Override
	public Optional<ID> getCurrentAuditor() {

		ID id = currentId();

		if (id == null)
			return Optional.empty();
		
		return Optional.of(id);
	}
}
