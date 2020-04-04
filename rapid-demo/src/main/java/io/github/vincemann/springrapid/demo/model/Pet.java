package io.github.vincemann.springrapid.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParentEntity;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.child.UniDirChildEntity;
import io.github.vincemann.springrapid.entityrelationship.model.uniDir.parent.UniDirParent;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pets")
@Entity
@Builder
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
