package com.github.vincemann.springrapid.syncdemo.dto.owner;

import com.github.vincemann.springrapid.autobidir.dto.child.annotation.BiDirChildId;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

public class ReadOwnOwnerDto extends ReadForeignOwnerDto {
    public static final String DIRTY_SECRET = "can you see this?";
    private String dirtySecret = DIRTY_SECRET;
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @Builder(builderMethodName = "Builder")
    public ReadOwnOwnerDto(Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, String dirtySecret, @NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName,Set<String> hobbies, Long clinicCardId) {
        super(petIds, address, city, telephone,hobbies);
        this.clinicCardId =clinicCardId;
        this.dirtySecret = dirtySecret;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
