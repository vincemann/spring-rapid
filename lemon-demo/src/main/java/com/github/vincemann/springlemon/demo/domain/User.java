package com.github.vincemann.springlemon.demo.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.util.UserVerifyUtils;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name="usr")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class User extends AbstractUser<Long> {

    private static final long serialVersionUID = 2716710947175132319L;

    public static final int NAME_MIN = 1;
    public static final int NAME_MAX = 50;

	public User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	@Builder
	public User(String email, String password, String name,Set<String> roles, String newEmail, long credentialsUpdatedMillis, String captchaResponse) {
		super(email, password, roles, newEmail, credentialsUpdatedMillis, captchaResponse);
		this.name = name;
	}

	@JsonView(UserVerifyUtils.SignupInput.class)
	@NotBlank(message = "{blank.name}", groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class})
    @Size(min=NAME_MIN, max=NAME_MAX, groups = {UserVerifyUtils.SignUpValidation.class, UserVerifyUtils.UpdateValidation.class})
    @Column(nullable = false, length = NAME_MAX)
    private String name;



}