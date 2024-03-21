package com.github.vincemann.springrapid.syncdemo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "toys",uniqueConstraints = @UniqueConstraint(name = "unique name", columnNames = "name"))
@Entity
@Builder
public class Toy extends IdAwareEntityImpl<Long> {


    @NotEmpty
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Override
    public String toString() {
        return "Toy{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", name='" + name + '\'' +
                ", pet=" + LazyToStringUtil.toStringIfLoaded(pet,Pet::getName) +
                '}';
    }
}
