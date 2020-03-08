package io.github.vincemann.demo.dtos.owner;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildIdCollection;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
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
