package com.github.vincemann.springrapid.syncdemo.dto.owner;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor

@Getter @Setter
public class UpdateOwnerDto extends AbstractOwnerDto  {

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @Builder
    public UpdateOwnerDto(@Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Size(min = 10, max = 10) String telephone, Set<Long> petIds,Set<String> hobbies) {
        super(address, city, telephone,hobbies);
        if (petIds != null)
            this.petIds = petIds;

    }

    @Size(min=3,max=255)
    @NotBlank
    public String getCity(){
        return city;
    }

    @NotNull
    @Override
    public Long getId() {
        return super.getId();
    }


}
