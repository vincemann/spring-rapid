package vincemann.github.generic.crud.lib.demo.dtos;


import lombok.*;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class PersonDTO extends IdentifiableEntityImpl<Long> {
    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;
}
