package vincemann.github.generic.crud.lib.demo.model;

import vincemann.github.generic.crud.lib.entityListener.BiDirParentEntityListener;
import vincemann.github.generic.crud.lib.demo.model.abs.Person;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vincemann.github.generic.crud.lib.model.biDir.BiDirChildCollection;
import vincemann.github.generic.crud.lib.model.biDir.BiDirParent;

import javax.persistence.*;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "owners")
@EntityListeners(BiDirParentEntityListener.class)
public class Owner extends Person implements BiDirParent {


    @Builder
    public Owner(String firstName, String lastName, Set<Pet> pets, String address, String city, String telephone) {
        super(firstName, lastName);
        if(pets!=null) {
            this.pets = pets;
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner",fetch = FetchType.EAGER)
    @JsonManagedReference
    @BiDirChildCollection(Pet.class)
    private Set<Pet> pets = new HashSet<>();


    @Column(name = "adress")
    private String address;
    @Column(name = "city")
    private String city;
    @Column(name = "telephone")
    private String telephone;

}
