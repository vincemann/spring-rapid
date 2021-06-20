package com.github.vincemann.springrapid.coredemo.dtos;

import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.annotation.BiDirParentId;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ClinicCardDto {

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
}
