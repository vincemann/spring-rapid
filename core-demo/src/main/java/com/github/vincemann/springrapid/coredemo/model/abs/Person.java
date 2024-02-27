package com.github.vincemann.springrapid.coredemo.model.abs;

import com.github.vincemann.springrapid.core.model.audit.AuditingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
public abstract class Person extends AuditingEntity<Long> {
    @Column(name = "first_name", nullable = false)
    @NotBlank
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, unique = true)
    private String lastName;
}
