package io.github.vincemann.demo.dtos;

import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Validated
@ToString
public class VetDTO extends PersonDTO {


    @Builder
    public VetDTO(@NotBlank @Size(min = 2, max = 20) String firstName, @NotBlank @Size(min = 2, max = 20) String lastName, @Nullable @Size(max = 20) Set<SpecialtyDTO> specialties) {
        super(firstName, lastName);
        if(specialties==null){
            this.specialties=new HashSet<>();
        }else {
            this.specialties = specialties;
        }
    }


    @Nullable
    @Size(max = 20)
    private Set<SpecialtyDTO> specialties;
}
