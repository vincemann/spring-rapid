package com.github.vincemann.springrapid.acldemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.acldemo.model.abs.Person;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.UniDirChildEntity;


import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "owners")
public class Owner extends Person implements UserAwareEntity {

    public Owner() {
    }

    @Builder
    public Owner(String firstName, String lastName, Set<Pet> pets, String address, String city, String telephone,Set<String> hobbies) {
        super(firstName, lastName);
        if(pets!=null) {
            this.pets = pets;
        }
        if(hobbies!=null) {
            this.hobbies = hobbies;
        }

        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @Override
    public String getAuthenticationName() {
        return getUser().getAuthenticationName();
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    @BiDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();

    @NotNull
    @UniDirChildEntity
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;


    @ElementCollection(targetClass = String.class,fetch = FetchType.EAGER)
    private Set<String> hobbies = new HashSet<>();

    @Column(name = "adress")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    private String telephone;



    @Override
    public String toString() {
        return "Owner{" +
                "super.toString()" +
                "user = " + user +
                "pets=" + Arrays.toString(pets.stream().map(Pet::getName).toArray()) +
                ", hobbies='"+hobbies+"'" +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

}
