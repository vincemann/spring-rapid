package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.entity.annotation.child.BiDirChildEntity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "owners")
public class Owner extends Person {

    public static final String DIRTY_SECRET = "can you see this?";



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

    // dont use remove cascade to showcase unlink on remove owner,
    @OneToMany(cascade = {PERSIST, MERGE, REFRESH, DETACH}, mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    @BiDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();

    @BiDirChildEntity
    // dont use remove cascade to showcase unlink on remove owner
    @OneToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_card_id",referencedColumnName = "id")
    private ClinicCard clinicCard;


    @ElementCollection(targetClass = String.class,fetch = FetchType.EAGER)
    private Set<String> hobbies = new HashSet<>();


    @Column(name = "adress")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "dirty_secret")
    private String dirtySecret = DIRTY_SECRET;

    @Override
    public String toString() {
        return "Owner{" +
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
