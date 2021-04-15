package com.github.vincemann.springrapid.coredemo.dtos.owner;

import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.child.annotation.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.parent.BiDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ReadForeignOwnerDto extends AbstractOwnerDto implements BiDirParentDto {

    @Builder
    public ReadForeignOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone,Set<String> hobbies) {
        super(address, city, telephone,hobbies);
        this.petIds=petIds;
    }

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

}
