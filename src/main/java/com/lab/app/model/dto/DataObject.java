package com.lab.app.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataObject {
    private Long id;
    private String key;
    private String value;
    private LocalDateTime timestamp;
    private Long version = 1L;

}
