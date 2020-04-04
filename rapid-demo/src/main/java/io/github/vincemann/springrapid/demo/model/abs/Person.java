package io.github.vincemann.springrapid.demo.model.abs;

import lombok.*;
import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
public abstract class Person extends IdentifiableEntityImpl<Long> {
    @Column(name = "first_name")
    @NotBlank
    private String firstName;
    @NotBlank
    @Column(name = "last_name")
    private String lastName;
}
