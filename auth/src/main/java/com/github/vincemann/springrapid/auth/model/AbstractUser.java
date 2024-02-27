package com.github.vincemann.springrapid.auth.model;

import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter @Setter
@MappedSuperclass
@NoArgsConstructor
public class AbstractUser<Id extends Serializable>
		extends AuditingEntity<Id>
{
	public static final int CONTACT_INFORMATION_MAX = 250;

	// contactInformation can be email or phone number - dont hardcode to email constraint
	@NotBlank
	@Column(nullable = false, unique = true, length = CONTACT_INFORMATION_MAX)
	protected String contactInformation;

	// in the contactInformation-change process, temporarily stores the new contactInformation
	@Nullable
	@Column(length = CONTACT_INFORMATION_MAX)
	protected String newContactInformation;

	// password
	@NotBlank
	@Column(nullable = false) // no length because it will be encrypted
	protected String password;

	// roles collection
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", nullable = false))
	@Column(name = "role", nullable = false)
	@NotEmpty
	protected Set<String> roles = new HashSet<>();



	// A JWT issued before this won't be valid
	@NotNull
	@Column(nullable = false)
	protected Long credentialsUpdatedMillis = System.currentTimeMillis();


	public AbstractUser(String contactInformation, String newContactInformation, String password, Set<String> roles, Long credentialsUpdatedMillis) {
		this.contactInformation = contactInformation;
		this.newContactInformation = newContactInformation;
		this.password = password;
		this.roles = roles;
		this.credentialsUpdatedMillis = credentialsUpdatedMillis;
	}

	public final boolean hasRole(String role) {
		return roles.contains(role);
	}

}

