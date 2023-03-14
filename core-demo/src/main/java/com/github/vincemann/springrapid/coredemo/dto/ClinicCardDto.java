package com.github.vincemann.springrapid.coredemo.dto;

import com.github.vincemann.springrapid.coredemo.dto.abs.MyIdDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Owner;

import com.github.vincemann.springrapid.autobidir.dto.parent.annotation.BiDirParentId;
import lombok.*;

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
