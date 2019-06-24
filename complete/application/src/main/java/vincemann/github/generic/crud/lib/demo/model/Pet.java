package vincemann.github.generic.crud.lib.demo.model;

import vincemann.github.generic.crud.lib.entityListener.BiDirChildEntityListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;
import vincemann.github.generic.crud.lib.model.biDir.BiDirChild;
import vincemann.github.generic.crud.lib.model.biDir.BiDirParentEntity;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets")
@Entity
@Builder
@EntityListeners(BiDirChildEntityListener.class)
public class Pet extends IdentifiableEntityImpl<Long> implements BiDirChild {

    @Column(name = "name")
    private String name;



    //hier sage ich dass ist eine unilaterale manytoone
    //-> dh PetType weiß nix davon
    //und es soll doch bitte die spalte mit dem namen
    //pet_type_id erstellt werden wo dann die id von petType rein kommt
    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    private PetType petType;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    @BiDirParentEntity
    private Owner owner;


    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

}
