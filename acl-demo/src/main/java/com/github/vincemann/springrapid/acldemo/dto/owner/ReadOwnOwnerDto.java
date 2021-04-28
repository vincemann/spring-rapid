package com.github.vincemann.springrapid.acldemo.dto.owner;

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
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @Builder(builderMethodName = "Builder")
    public ReadOwnOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, String dirtySecret, @NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName,Set<String> hobbies) {
        super(petIds, address, city, telephone,hobbies);
        this.dirtySecret = dirtySecret;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
