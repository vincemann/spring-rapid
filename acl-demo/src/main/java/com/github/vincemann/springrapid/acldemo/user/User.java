package com.github.vincemann.springrapid.acldemo.user;

import com.github.vincemann.springrapid.auth.AbstractUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usr",uniqueConstraints = {
        @UniqueConstraint(name = "unique last name", columnNames = {"lastName"}),
        @UniqueConstraint(name = "unique email", columnNames = "contactInformation")
})
@NoArgsConstructor
@Getter
@Setter
public abstract class User extends AbstractUser<Long> {

    @NotBlank
    @Column
    private String firstName;

    @NotBlank
    @Column
    private String lastName;
    @Email
    @NotBlank
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
