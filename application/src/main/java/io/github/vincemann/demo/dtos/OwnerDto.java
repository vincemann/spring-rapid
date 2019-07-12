package io.github.vincemann.demo.dtos;


import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirChildIdCollection;
import io.github.vincemann.generic.crud.lib.dto.biDir.BiDirDtoParent;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OwnerDto extends PersonDto implements BiDirDtoParent {

    @Builder
    public OwnerDto(@NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName, @Nullable Set<Long> petIds, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Nullable @Size(min = 10, max = 10) String telephone) {
        super(firstName, lastName);
        if(petIds==null){
            this.petIds= new HashSet<>();
        }else {
            this.petIds = petIds;
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @Nullable
    @BiDirChildIdCollection(Pet.class)
    private Set<Long> petIds = new HashSet<>();

    @Size(min=10,max=255)
    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @Size(min=10,max=10)
    @Nullable
    private String telephone;
}
