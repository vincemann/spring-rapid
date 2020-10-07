package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.io.Serializable;
import java.util.Optional;

/**
 * Needed for auto-filling of the
 * AbstractAuditable columns of AbstractUser
 *  
 * @author Sanjay Patel
 */
@Slf4j
public abstract class AbstractAuditorAware<ID extends Serializable>
				implements AuditorAware<ID> {


//    private IdConverter<ID> idConverter;

	protected abstract ID currentId();
	
	@Override
	public Optional<ID> getCurrentAuditor() {

		ID id = currentId();

		if (id == null)
			return Optional.empty();
		
		return Optional.of(id);
	}

//	@Autowired
//	public void injectIdConverter(IdConverter<ID> idConverter) {
//		this.idConverter = idConverter;
//
//	}
}
