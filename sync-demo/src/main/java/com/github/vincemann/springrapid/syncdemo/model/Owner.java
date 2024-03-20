package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.syncdemo.model.abs.Person;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "owners", uniqueConstraints = @UniqueConstraint(name = "unique last name", columnNames = "last_name"))
public class Owner extends Person {

    public static final String SECRET = "mySecret";

    @OneToMany(mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Pet> pets = new HashSet<>();

    @BiDirChildEntity
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_card_id",referencedColumnName = "id")
    private ClinicCard clinicCard;


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
    public Owner(String firstName, String lastName, Set<Pet> pets, String address, String city, String telephone, Set<String> hobbies) {
        super(firstName, lastName);
        if(pets!=null) {
            this.pets = pets;
        }else {
            this.pets = new HashSet<>();
        }
        if(hobbies!=null) {
            this.hobbies = hobbies;
        }else{
            this.hobbies = new HashSet<>();
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }


    @Override
    public String toString() {
        return "Owner{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", pets=" + LazyToStringUtil.toStringIfLoaded(pets,Pet::getName) +
                ", clinicCard=" + LazyToStringUtil.toStringIfLoaded(clinicCard, c -> c.getRegistrationDate().toString()) +
                ", hobbies=" + hobbies +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
