package com.lab.app.dao;

import com.lab.app.model.entity.KeyValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ObjectRepository extends JpaRepository<KeyValue, Long> {

    /**
     * find latest result by kvKey, and with descending order of created field
     */
    Optional<KeyValue> findTopByKvKeyOrderByCreatedDesc(String key);

    /**
     * find latest result by kvKey and if it's less than given timestamp
     */
    Optional<KeyValue> findTopByKvKeyAndCreatedLessThanEqualOrderByCreatedDesc(String key, LocalDateTime timestamp);

}
