package com.github.vincemann.springrapid.acldemo.model;

import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @BiDirChildCollection(Specialty.class)
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
                ", createdById=" + getCreatedById() +
                ", createdDate=" + getCreatedDate() +
                ", lastModifiedById=" + getLastModifiedById() +
                ", lastModifiedDate=" + getLastModifiedDate() +
                ", id=" + getId() +
                '}';
    }
}
