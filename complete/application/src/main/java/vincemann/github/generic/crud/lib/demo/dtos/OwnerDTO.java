package vincemann.github.generic.crud.lib.demo.dtos;


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
public class OwnerDTO extends PersonDTO {

    @Builder
    public OwnerDTO(@NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName, @Nullable Set<PetDTO> pets, @Size(min = 10, max = 255) @NotBlank String address, @NotBlank String city, @Nullable @Size(min = 10, max = 10) String telephone) {
        super(firstName, lastName);
        if(pets==null){
            this.pets= new HashSet<>();
        }else {
            this.pets = pets;
        }
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    @Nullable
    private Set<PetDTO> pets;
    @Size(min=10,max=255)
    @NotBlank
    private String address;
    @NotBlank
    private String city;

    @Size(min=10,max=10)
    @Nullable
    private String telephone;
}
