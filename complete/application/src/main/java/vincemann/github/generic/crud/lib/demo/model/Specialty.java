package vincemann.github.generic.crud.lib.demo.model;

import lombok.*;
import vincemann.github.generic.crud.lib.model.IdentifiableEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Specialty extends IdentifiableEntityImpl<Long> {

    @Column(name = "description")
    private String description;
}
