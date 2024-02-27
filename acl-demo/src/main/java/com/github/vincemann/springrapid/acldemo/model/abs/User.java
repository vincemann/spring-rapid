package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.springrapid.auth.model.AbstractUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@Getter
@Setter
public abstract class User extends AbstractUser<Long> {

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;
    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = CONTACT_INFORMATION_MAX)
    @Override
    public String getContactInformation() {
        return super.getContactInformation();
    }

    public User(String contactInformation, String newContactInformation, String password, Set<String> roles, Long credentialsUpdatedMillis, String firstName, String lastName) {
        super(contactInformation, newContactInformation, password, roles, credentialsUpdatedMillis);
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
