package com.github.vincemann.springrapid.syncdemo.dto.owner.own;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.syncdemo.dto.owner.AbstractOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * has first name and last name
 */
@NoArgsConstructor
@Getter
@Setter
public class ReadOwnDetailedOwnerDto extends AbstractOwnerDto {
    public static final String DIRTY_SECRET = "can you see this?";
    private String dirtySecret = DIRTY_SECRET;

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;


    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    public ReadOwnDetailedOwnerDto(String address, String city, String telephone, Set<String> hobbies, String dirtySecret, @Nullable Set<Long> petIds, String lastName, String firstName, Long clinicCardId) {
        super(address, city, telephone, hobbies);
        this.dirtySecret = dirtySecret;
        this.petIds = petIds;
        this.lastName = lastName;
        this.firstName = firstName;
        this.clinicCardId = clinicCardId;
    }
}
