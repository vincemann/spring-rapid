package vincemann.github.generic.crud.lib.demo.dtos;

import lombok.*;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class PetTypeDTO extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}
