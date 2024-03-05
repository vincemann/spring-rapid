package com.github.vincemann.springrapid.authdemo.model;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import com.github.vincemann.springrapid.authdemo.service.ValidUsername;
import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "usr", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"contactInformation"}),
		@UniqueConstraint(columnNames = {"name"})
})
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractUser<Long> {

    private static final long serialVersionUID = 2716710947175132319L;


	public User(String contactInformation, String password, String name, String... roles) {
		this.contactInformation = contactInformation;
		this.password = password;
		this.roles= Sets.newHashSet(roles);
		this.name = name;
	}

	@Builder
	public User(String contactInformation, String password, String name, Set<String> roles, String newContactInformation, long credentialsUpdatedMillis) {
		super(contactInformation,newContactInformation, password, roles, credentialsUpdatedMillis);
		this.name = name;
	}

	@NotBlank(message = "{blank.name}")
    @ValidUsername
    @Column(nullable = false,unique = true, length = ValidUsername.MAX_SIZE)
    private String name;

	@Email
	@Column(nullable = true, length = CONTACT_INFORMATION_MAX)
	public String getNewContactInformation() {
		return newContactInformation;
	}

	@NotBlank
	@Email
	@Override
	@Column(nullable = false, unique = true, length = CONTACT_INFORMATION_MAX)
	public String getContactInformation() {
		return super.getContactInformation();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
	}
}