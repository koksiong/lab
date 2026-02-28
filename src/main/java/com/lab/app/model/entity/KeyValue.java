package com.lab.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uc_key_version", columnNames = {"kvKey", "version"})
})
@Data
public class KeyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String kvKey;
    @Column(length = 1000)
    private String kvValue;
    private Long version;
    private LocalDateTime created;

}
