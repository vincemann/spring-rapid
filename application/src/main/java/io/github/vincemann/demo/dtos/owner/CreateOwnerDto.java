package io.github.vincemann.demo.dtos.owner;


import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirParentDto;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class CreateOwnerDto extends BaseOwnerDto implements BiDirParentDto {

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @Null
    @Override
    public Long getId() {
        return super.getId();
    }

    @Builder
    public CreateOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, @NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName) {
        super(petIds, address, city, telephone);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @NotBlank
    @Override
    public @Size(min = 10, max = 255) String getAddress() {
        return super.getAddress();
    }

    @NotBlank
    @Override
    public @Size(min=3,max=255) String getCity() {
        return super.getCity();
    }

    @Nullable
    @Override
    public @Size(min = 10, max = 10) String getTelephone() {
        return super.getTelephone();
    }
}
