package com.github.vincemann.springrapid.syncdemo.model;

import com.github.vincemann.springrapid.autobidir.entity.annotation.parent.BiDirParentEntity;
import com.github.vincemann.springrapid.core.model.IdAwareEntityImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "clinic_cards")
public class ClinicCard extends IdAwareEntityImpl<Long> {


    @BiDirParentEntity
    @OneToOne(mappedBy = "clinicCard")
    private Owner owner;
    @NotNull
    private Date registrationDate;
    @NotNull
    private String registrationReason;

    @Builder
    public ClinicCard(Owner owner, Date registrationDate, String registrationReason) {
        this.owner = owner;
        this.registrationDate = registrationDate;
        this.registrationReason = registrationReason;
    }

    @Override
    public String toString() {
        return "ClinicCard{" +
                "id=" + (getId() == null ? "null" : getId().toString()) +
                ", owner=" + ((owner ==  null) ? "null" : owner.getLastName()) +
                ", registrationDate=" + registrationDate +
                ", registrationReason='" + registrationReason + '\'' +
                ", id=" + getId() +
                '}';
    }
}
