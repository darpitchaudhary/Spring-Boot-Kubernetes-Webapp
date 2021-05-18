package com.csye7125.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertReturn {

    private UUID id;
    private String category;
    private String keyword;
    private String expiry;
    private String alert_created;
    private String alert_expiry;
    private UserReturn user;

}

