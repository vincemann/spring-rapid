package com.github.vincemann.springrapid.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Base class for all entities that want auditing.
 * 
 * @author Sanjay Patel
 * @modifiedBy vincemann
  */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "createdById", "lastModifiedById", "createdDate", "lastModifiedDate", "new" })
@EntityListeners(AuditingEntityListener.class)
public class AuditingEntity<ID extends Serializable>
			extends IdentifiableEntityImpl<ID>
				implements IAuditingEntity<ID>
{

	private static final long serialVersionUID = -8151190931948396443L;
	public static final String LAST_MOD_FIELD = "lastModifiedDate";
//	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	
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
}
