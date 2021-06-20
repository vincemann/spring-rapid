package com.github.vincemann.springrapid.coredemo.dtos.owner;

import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true)
@Getter @Setter
public class UpdateOwnerDto extends AbstractOwnerDto implements BiDirParentDto {

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @Builder
    public UpdateOwnerDto(@Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, Set<Long> petIds,Set<String> hobbies) {
        super(address, city, telephone,hobbies);
        if (petIds != null)
            this.petIds = petIds;

    }

    @NotNull
    @Override
    public Long getId() {
        return super.getId();
    }
}
