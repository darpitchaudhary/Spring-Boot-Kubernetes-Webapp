package com.csye7125.notifier.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Alert")
@NoArgsConstructor
@Getter
@Setter

public class Alert {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "category")
    private String category;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "expiry")
    private String expiry;

    @Column(name = "alert_created")
    private String alert_created;

    @Column(name = "alert_expiry")
    private String alert_expiry;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Alert(String category, String keyword, String expiry, String alert_created, String alert_expiry, User user) {
        this.category = category;
        this.keyword = keyword;
        this.expiry = expiry;
        this.alert_created = LocalDateTime.now().toString();
        this.alert_expiry = LocalDateTime.now().plusMinutes(Long.parseLong(expiry)).toString();
        this.user = user;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", keyword='" + keyword + '\'' +
                ", expiry='" + expiry + '\'' +
                ", alert_created='" + alert_created + '\'' +
                ", alert_expiry='" + alert_expiry + '\'' +
                ", user=" + user +
                '}';
    }
}

