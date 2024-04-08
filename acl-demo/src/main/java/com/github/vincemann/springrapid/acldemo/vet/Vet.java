package com.github.vincemann.springrapid.acldemo.vet;

import com.github.vincemann.springrapid.acldemo.other.Specialty;
import com.github.vincemann.springrapid.acldemo.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vets")
public class Vet extends User {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialtys",
            joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    private Set<Specialty> specialtys = new HashSet<>();


    @Builder
    public Vet(String contactInformation, String newContactInformation, String password, Set<String> roles, Long credentialsUpdatedMillis, String firstName, String lastName, Set<Specialty> specialtys) {
        super(contactInformation, newContactInformation, password, roles, credentialsUpdatedMillis, firstName, lastName);
        if (specialtys != null)
            this.specialtys = specialtys;
    }

    @Override
    public String toString() {
        return "Vet{" +
                "specialtys=" + specialtys +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", contactInformation='" + getContactInformation() + '\'' +
                ", newContactInformation='" + getNewContactInformation() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", roles=" + getRoles() +
                ", credentialsUpdatedMillis=" + getCredentialsUpdatedMillis() +
                ", id=" + getId() +
                '}';
    }
}
