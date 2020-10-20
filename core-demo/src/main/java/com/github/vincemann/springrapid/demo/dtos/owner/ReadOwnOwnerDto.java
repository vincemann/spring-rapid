package com.github.vincemann.springrapid.demo.dtos.owner;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ReadOwnOwnerDto extends ReadForeignOwnerDto {
    public static final String DIRTY_SECRET = "can you see this?";
    private String dirtySecret = DIRTY_SECRET;

    @Builder(builderMethodName = "Builder")
    public ReadOwnOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, String dirtySecret) {
        super(petIds, address, city, telephone);
        this.dirtySecret = dirtySecret;
    }
}
