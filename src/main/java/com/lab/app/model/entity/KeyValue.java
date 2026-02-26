package com.lab.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
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
