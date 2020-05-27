package io.github.spring.lemon.auth.domain;

import io.github.spring.lemon.auth.domain.dto.user.LemonUserDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class AbstractAuditorAware<ID extends Serializable>
implements AuditorAware<ID> {

    private static final Log log = LogFactory.getLog(AbstractAuditorAware.class);
    
    private IdConverter<ID> idConverter;
    
    @Autowired
	public void setIdConverter(IdConverter<ID> idConverter) {
		
		this.idConverter = idConverter;
		log.info("Created");
	}

	protected abstract LemonUserDto currentUser();
	
	@Override
	public Optional<ID> getCurrentAuditor() {
		
		LemonUserDto user = currentUser();
		
		if (user == null)
			return Optional.empty();
		
		return Optional.of(idConverter.toId(user.getId()));
	}	
}
