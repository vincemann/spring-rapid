package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.auth.domain.AbstractUser;
import com.google.common.collect.Sets;
import com.sun.istack.Nullable;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name="usr")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class User extends AbstractUser<Long> {

	// only used for register purposes, to prevent guessing attacks and will be null after registration process
	@Nullable
	private String uuid;

	public User(String email, String password, String uuid, String... roles) {
		this.email = email;
		this.password = password;
		this.roles= Sets.newHashSet(roles);
		this.uuid = uuid;
	}

	@Builder
	public User(String email, String password, String uuid, Set<String> roles, String newEmail, long credentialsUpdatedMillis, String captchaResponse) {
		super(email, password, roles, newEmail, credentialsUpdatedMillis, captchaResponse);
		this.uuid = uuid;
	}



}