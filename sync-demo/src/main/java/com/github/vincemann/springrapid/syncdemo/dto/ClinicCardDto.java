package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.resolveid.annotation.parent.BiDirParentId;
import com.github.vincemann.springrapid.syncdemo.dto.abs.IdAwareDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ClinicCardDto extends IdAwareDto {

    @Positive
    @BiDirParentId(Owner.class)
    private Long ownerId;
    @NotNull
    private Date registrationDate;
    @NotEmpty
    private String registrationReason;

    @Builder
    public ClinicCardDto(Long ownerId, Date registrationDate, String registrationReason, Long id) {
        super(id);
        this.ownerId = ownerId;
        this.registrationDate = registrationDate;
        this.registrationReason = registrationReason;
    }

    public ClinicCardDto(ClinicCard clinicCard){
        super(clinicCard.getId());
        this.ownerId = clinicCard.getOwner() == null ? null : clinicCard.getOwner().getId();
        this.registrationDate = clinicCard.getRegistrationDate();
        this.registrationReason = clinicCard.getRegistrationReason();
    }

    @Override
    public String toString() {
        return "ClinicCardDto{" +
                "ownerId=" + ownerId +
                ", registrationDate=" + registrationDate +
                ", registrationReason='" + registrationReason + '\'' +
                ", id=" + getId() +
                '}';
    }
}
