package com.github.vincemann.springrapid.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Getter @Setter
@MappedSuperclass
@AllArgsConstructor
public class AbstractUser<ID extends Serializable>
	extends AuditingEntity<ID>
		implements AuthenticatingEntity<ID>
{

	// contactInformation can be email or phone number ... dont hardcode to email!
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@UniqueContactInformation(groups = {UserVerifyUtils.SignUpValidation.class})
//	@ContactInformation
	@Column(nullable = false, unique = true, length = UserVerifyUtils.CONTACT_INFORMATION_MAX)
	protected String contactInformation;

	// password
	// @NotBlank gets checked by PasswordChecker
	// todo change to use @Password Annotation
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeContactInformationValidation.class}*/)
	@Column(nullable = false) // no length because it will be encrypted
	protected String password;

	// roles collection
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	protected Set<String> roles = new HashSet<>();

	// in the contactInformation-change process, temporarily stores the new contactInformation
//	@UniqueContactInformation(groups = {UserVerifyUtils.ChangeContactInformationValidation.class})
	@Column(length = UserVerifyUtils.CONTACT_INFORMATION_MAX)
	protected String newContactInformation;

	// A JWT issued before this won't be valid
	@Column(nullable = false)
	@JsonIgnore
	protected Long credentialsUpdatedMillis = System.currentTimeMillis();

	// holds reCAPTCHA response while signing up
	// todo put captcha response in signupDto and validate there
	@Transient
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@Captcha(groups = {UserVerifyUtils.SignUpValidation.class})
	private String captchaResponse;

	public final boolean hasRole(String role) {
		return roles.contains(role);
	}

	public AbstractUser() {
	}

	@Override
	public String getAuthenticationName() {
		return this.contactInformation;
	}
}

