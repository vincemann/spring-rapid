package io.github.spring.lemon.auth.security.domain;

import io.github.spring.lemon.auth.domain.AbstractAuditorAware;
import io.github.spring.lemon.auth.domain.dto.user.LemonUserDto;
import io.github.spring.lemon.auth.util.LecwUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Needed for auto-filling of the
 * AbstractAuditable columns of AbstractUser
 *  
 * @author Sanjay Patel
 */
public class LemonAuditorAware<ID extends Serializable>
extends AbstractAuditorAware<ID> {
	
    private static final Log log = LogFactory.getLog(LemonAuditorAware.class);
    
	public LemonAuditorAware() {
		log.info("Created");
	}

	@Override
	protected LemonUserDto currentUser() {
		return LecwUtils.currentUser();
	}	
}
