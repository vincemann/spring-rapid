package com.naturalprogrammer.spring.lemon.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

/**
 * Base class for all entities.
 * 
 * @author Sanjay Patel
  */
//todo alle meine Entites sollten hier von erben -> meine TimeStamp abstract Entity raushauen
@MappedSuperclass
@Getter @Setter
@JsonIgnoreProperties({ "createdById", "lastModifiedById", "createdDate", "lastModifiedDate", "new" })
public class LemonEntity<ID extends Serializable> extends IdentifiableEntityImpl<ID>
		//todo rausnehmen und überAll wo hasPermission gecallt wird an SecurityChecker delegaten
{

	private static final long serialVersionUID = -8151190931948396443L;
	
	@CreatedBy
	private ID createdById;
	
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	
	@LastModifiedBy
	private ID lastModifiedById;
	
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;
	
	@Version
	private Long version;

	
}
