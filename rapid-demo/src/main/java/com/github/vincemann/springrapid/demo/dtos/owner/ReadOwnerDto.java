package com.github.vincemann.springrapid.demo.dtos.owner;

import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildIdCollection;
import com.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
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
public class ReadOwnerDto extends AbstractOwnerDto implements BiDirParentDto {

    @Builder
    public ReadOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone) {
        super(address, city, telephone);
        this.petIds=petIds;
    }

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

}
