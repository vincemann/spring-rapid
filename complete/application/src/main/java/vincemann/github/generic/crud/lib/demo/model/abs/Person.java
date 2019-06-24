package vincemann.github.generic.crud.lib.demo.model.abs;

import lombok.*;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
public abstract class Person extends IdentifiableEntityImpl<Long> {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
}
