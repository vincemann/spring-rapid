package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public abstract class Person extends IdentifiableEntityImpl<Long> {
    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @ToString.Include
    @Unique
    @NotBlank
    @Column(name = "last_name")
    private String lastName;
}
