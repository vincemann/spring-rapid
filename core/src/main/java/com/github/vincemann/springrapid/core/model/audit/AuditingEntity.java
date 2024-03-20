package com.github.vincemann.springrapid.core.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
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
@JsonIgnoreProperties({ "createdById", "lastModifiedById", "createdDate", "lastModifiedDate", "new" })
@EntityListeners(AuditingEntityListener.class)
public class AuditingEntity<ID extends Serializable>
			extends IdAwareEntityImpl<ID>
				implements IAuditingEntity<ID>
{

	private static final long serialVersionUID = -8151190931948396443L;
	public static final String LAST_MOD_FIELD = "lastModifiedDate";

	
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

	public AuditingEntity() {
	}

	@Override
	public ID getCreatedById() {
		return createdById;
	}

	public void setCreatedById(ID createdById) {
		this.createdById = createdById;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public ID getLastModifiedById() {
		return lastModifiedById;
	}

	@Override
	public void setLastModifiedById(ID lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	@Override
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
