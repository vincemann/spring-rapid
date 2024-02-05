package com.github.vincemann.springrapid.sync.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
public class EntityDtoMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "entity_class", referencedColumnName = "entityClass"),
            @JoinColumn(name = "entity_id", referencedColumnName = "entityId")
    })
    @NotNull
    private AuditLog auditLog;

    @Column(name = "dto_class")
    @NotNull
    private String dtoClass;

    @NotNull
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    @Builder
    public EntityDtoMapping(AuditLog auditLog, String dtoClass, Date lastUpdateTime) {
        this.auditLog = auditLog;
        this.dtoClass = dtoClass;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "EntityDtoMapping{" +
                "id=" + id +
                ", auditLog=" + auditLog == null ? "null" : auditLog.toShortString() +
                ", dtoClass='" + dtoClass + '\'' +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
