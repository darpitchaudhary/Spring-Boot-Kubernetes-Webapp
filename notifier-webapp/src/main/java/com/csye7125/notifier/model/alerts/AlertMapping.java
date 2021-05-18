package com.csye7125.notifier.model.alerts;

import lombok.*;
import org.apache.kafka.common.protocol.types.Field;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@ToString
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AlertMapping")
public class AlertMapping {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "alert_id", updatable = false, nullable = false)
    private UUID alert_id;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "title")
    private String title;

    @Column(name = "is_done")
    private Boolean is_done;

    @Column(name = "user_id")
    private UUID user_id;

    public AlertMapping(String keyword, String title, Boolean is_done, UUID user_id) {
        this.keyword=keyword;
        this.title=title;
        this.is_done=is_done;
        this.user_id=user_id;
    }
}