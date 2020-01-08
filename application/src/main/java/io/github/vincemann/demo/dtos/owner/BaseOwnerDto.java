package io.github.vincemann.demo.dtos.owner;

import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildIdCollection;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentDto;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntityImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class BaseOwnerDto extends IdentifiableEntityImpl<Long> implements BiDirParentDto {

    public BaseOwnerDto(@Nullable Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Nullable @Size(min = 10, max = 10) String telephone) {
        this.petIds = petIds;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @Size(min=10,max=255)
    private String address;

    @Size(min=3,max=255)
    private String city;

    @Size(min=10,max=10)
    private String telephone;
}
