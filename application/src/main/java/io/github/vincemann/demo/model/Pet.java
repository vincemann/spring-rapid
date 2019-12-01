package io.github.vincemann.demo.model;

import io.github.vincemann.generic.crud.lib.model.entityListener.BiDirChildEntityListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.github.vincemann.generic.crud.lib.model.uniDir.child.UniDirChildEntity;
import io.github.vincemann.generic.crud.lib.model.uniDir.parent.UniDirParent;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import io.github.vincemann.generic.crud.lib.model.biDir.child.BiDirChild;
import io.github.vincemann.generic.crud.lib.model.biDir.parent.BiDirParentEntity;

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
@ToString
public class Pet extends IdentifiableEntityImpl<Long> implements BiDirChild, UniDirParent {

    @Column(name = "name")
    private String name;



    //uniDir ManyToOne -> PetType does not know about this mapping
    @ManyToOne
    @JoinColumn(name = "pet_type_id")
    @UniDirChildEntity
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
