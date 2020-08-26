package com.github.vincemann.springlemon.auth.domain;

import com.github.vincemann.springlemon.auth.domain.dto.user.LemonUserDto;
import com.github.vincemann.springlemon.auth.util.LecwUtils;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		log.info("Created");
	}

	@Override
	protected ID currentId() {
		return idIdConverter.toId(securityContext.currentPrincipal().getId());
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
