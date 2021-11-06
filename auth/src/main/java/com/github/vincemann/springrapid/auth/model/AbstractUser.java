package com.github.vincemann.springrapid.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springrapid.auth.util.UserVerifyUtils;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
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

	// email
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@UniqueEmail(groups = {UserVerifyUtils.SignUpValidation.class})
	@Email
	@Column(nullable = false, unique = true, length = UserVerifyUtils.EMAIL_MAX)
	protected String email;

	// password
	// @NotBlank gets checked by PasswordChecker
	// todo change to use @Password Annotation
	@JsonView(UserVerifyUtils.SignupInput.class)
//	@Password(/*groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.ChangeEmailValidation.class}*/)
	@Column(nullable = false) // no length because it will be encrypted
	protected String password;

	// roles collection
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "usr_role", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	protected Set<String> roles = new HashSet<>();

	// in the email-change process, temporarily stores the new email
//	@UniqueEmail(groups = {UserVerifyUtils.ChangeEmailValidation.class})
	@Column(length = UserVerifyUtils.EMAIL_MAX)
	protected String newEmail;

	// A JWT issued before this won't be valid
	@Column(nullable = false)
	@JsonIgnore
	protected long credentialsUpdatedMillis = System.currentTimeMillis();

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
		return this.email;
	}
}

