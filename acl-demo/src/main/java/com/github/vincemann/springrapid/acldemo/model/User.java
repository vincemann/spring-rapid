package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.google.common.collect.Sets;
import com.sun.istack.Nullable;
import lombok.*;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name="usr")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractUser<Long> {

	// only used for register purposes, to prevent guessing attacks and will be null after registration process
	@Nullable
	private String uuid;

	public User(String contactInformation, String password, String uuid, String... roles) {
		this.contactInformation = contactInformation;
		this.password = password;
		this.roles= Sets.newHashSet(roles);
		this.uuid = uuid;
	}

	@Builder
	public User(String contactInformation, String password, String uuid, Set<String> roles, String newContactInformation, long credentialsUpdatedMillis) {
		super(contactInformation,newContactInformation, password, roles, credentialsUpdatedMillis);
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "User{" +
				"uuid='" + uuid + '\'' +
				", contactInformation='" + contactInformation + '\'' +
				", newContactInformation='" + newContactInformation + '\'' +
				", password='" + "hidden" + '\'' +
				", roles=" + roles +
				", credentialsUpdatedMillis=" + credentialsUpdatedMillis +
				", roles=" + getRoles() +
				", createdById=" + getCreatedById() +
				", createdDate=" + getCreatedDate() +
				", lastModifiedById=" + getLastModifiedById() +
				", lastModifiedDate=" + getLastModifiedDate() +
				", id=" + getId() +
				'}';
	}
}