package io.github.vincemann.demo.model;

import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirChildEntity;
import io.github.vincemann.generic.crud.lib.model.uniDir.UniDirParent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Visit extends IdentifiableEntityImpl<Long> implements UniDirParent {

    @ManyToOne
    @JoinColumn(name = "pet_id")
    @UniDirChildEntity
    private Pet pet;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description")
    private String description;
}
