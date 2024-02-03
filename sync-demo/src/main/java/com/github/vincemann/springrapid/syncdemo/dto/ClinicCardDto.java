package com.github.vincemann.springrapid.syncdemo.dto;

import com.github.vincemann.springrapid.autobidir.id.annotation.parent.BiDirParentId;
import com.github.vincemann.springrapid.syncdemo.dto.abs.MyIdDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor

public class ClinicCardDto extends MyIdDto<Long> {

    @BiDirParentId(Owner.class)
    private Long ownerId;

    private Date registrationDate;
    private String registrationReason;

    @Builder
    public ClinicCardDto(Long ownerId, Date registrationDate, String registrationReason) {
        this.ownerId = ownerId;
        this.registrationDate = registrationDate;
        this.registrationReason = registrationReason;
    }

    public ClinicCardDto(ClinicCard clinicCard){
        this.ownerId = clinicCard.getOwner() == null ? null : clinicCard.getOwner().getId();
        this.registrationDate = clinicCard.getRegistrationDate();
        this.registrationReason = clinicCard.getRegistrationReason();
    }
}
