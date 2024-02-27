package com.github.vincemann.springrapid.acldemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "owners", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"last_name"}),
        @UniqueConstraint(name = "unique email", columnNames = "contact_information")
})
public class Owner extends User {

    public static final String DIRTY_SECRET = "myDirtSecret";

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    @BiDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();


    @ElementCollection(targetClass = String.class,fetch = FetchType.EAGER)
    private Set<String> hobbies = new HashSet<>();

    @NotEmpty
    @Column(name = "adress", nullable = false)
    private String address;

    @NotEmpty
    @Column(name = "city", nullable = false)
    private String city;

    @Nullable
    @Column(name = "telephone", nullable = true)
    private String telephone;

    @Builder
    public Owner(String contactInformation, String newContactInformation, String password, Set<String> roles, Long credentialsUpdatedMillis, String firstName, String lastName, Set<Pet> pets, Set<String> hobbies, String address, String city, @Nullable String telephone) {
        super(contactInformation, newContactInformation, password, roles, credentialsUpdatedMillis, firstName, lastName);
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        if(pets!=null)
            this.pets = pets;
        if(hobbies!=null)
            this.hobbies = hobbies;
    }

    public Owner() {
    }

    @Override
    public String toString() {
        return "Owner{" +
                "pets=" + pets +
                ", hobbies=" + hobbies +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
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
