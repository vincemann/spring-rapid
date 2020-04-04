package io.github.vincemann.springrapid.demo.dtos.owner;

import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirChildIdCollection;
import io.github.vincemann.springrapid.entityrelationship.dto.biDir.BiDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReadOwnerDto extends BaseOwnerDto implements BiDirParentDto {

    @Builder
    public ReadOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone) {
        super(address, city, telephone);
        this.petIds=petIds;
    }

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

}
