package com.github.vincemann.springrapid.coredemo.model;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import com.github.vincemann.springrapid.entityrelationship.model.child.BiDirChild;
import com.github.vincemann.springrapid.entityrelationship.model.parent.annotation.BiDirParentEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "clinic_cards")
public class ClinicCard extends IdentifiableEntityImpl<Long> implements BiDirChild {


    @BiDirParentEntity
    @OneToOne(mappedBy = "clinicCard")
    private Owner owner;
    private Date registrationDate;
    private String registrationReason;

    @Builder
    public ClinicCard(Owner owner, Date registrationDate, String registrationReason) {
        this.owner = owner;
        this.registrationDate = registrationDate;
        this.registrationReason = registrationReason;
    }
}
