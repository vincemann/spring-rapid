package com.github.vincemann.springrapid.sync.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private AuditLog auditLog;

    @Column(name = "dto_class")
    private String dtoClass;

    @Column(name = "last_update_time")
    private LocalDateTime lastUpdateTime;

    @Builder
    public EntityDtoMapping(AuditLog auditLog, String dtoClass, LocalDateTime lastUpdateTime) {
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
