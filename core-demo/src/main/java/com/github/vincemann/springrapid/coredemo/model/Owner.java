package com.github.vincemann.springrapid.coredemo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.vincemann.springrapid.coredemo.model.abs.Person;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildCollection;
import com.github.vincemann.springrapid.autobidir.model.child.annotation.BiDirChildEntity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "owners")
public class Owner extends Person {


    @Builder
    public Owner(String firstName, String lastName, Set<Pet> pets, String address, String city, String telephone,Set<String> hobbies,Set<LazyExceptionItem> lazyExceptionItems, Set<LazyLoadedItem> lazyLoadedItems) {
        super(firstName, lastName);
        if(pets!=null) {
            this.pets = pets;
        }
        if(hobbies!=null) {
            this.hobbies = hobbies;
        }
        if(lazyExceptionItems !=null) {
            this.lazyExceptionItems = lazyExceptionItems;
        }
        if (lazyLoadedItems != null){
            this.lazyLoadedItems = lazyLoadedItems;
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    // dont use remove cascade to showcase unlink on remove owner
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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.LAZY)
    @BiDirChildCollection(LazyExceptionItem.class)
    @JsonManagedReference
    private Set<LazyExceptionItem> lazyExceptionItems = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.LAZY)
    @BiDirChildCollection(LazyExceptionItem.class)
    @JsonManagedReference
    private Set<LazyLoadedItem> lazyLoadedItems = new HashSet<>();

    @Column(name = "adress")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    private String telephone;



    @Override
    public String toString() {
        return "Owner{" +
//                super.toString() +
                "pets=" + Arrays.toString(pets.stream().map(Pet::getName).toArray()) +
                ", hobbies='"+hobbies+"'" +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

}
