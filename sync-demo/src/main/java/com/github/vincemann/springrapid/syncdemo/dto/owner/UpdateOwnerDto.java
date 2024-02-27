package com.github.vincemann.springrapid.syncdemo.dto.owner;

import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildId;
import com.github.vincemann.springrapid.autobidir.id.annotation.child.BiDirChildIdCollection;
import com.github.vincemann.springrapid.coredemo.dto.owner.abs.AbstractOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.ClinicCard;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOwnerDto extends AbstractOwnerDto {

    @NotNull
    @Positive
    private Long id;

    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @Positive
    @BiDirChildId(ClinicCard.class)
    private Long clinicCardId;

    @NotBlank
    @Size(min=3,max=255)
    @Override
    public String getCity(){
        return city;
    }

    @Nullable
    @Size(min=10,max=10)
    @Override
    public String getTelephone() {
        return super.getTelephone();
    }

    @NotEmpty
    @Size(min=3,max=255)
    @Override
    public String getAddress() {
        return super.getAddress();
    }

    @Builder
    public UpdateOwnerDto(String address, String city, String telephone, Set<Long> petIds,Set<String> hobbies, Long id) {
        super(address, city, telephone,hobbies);
        this.id = id;
        if (petIds != null)
            this.petIds = petIds;

    }


}
