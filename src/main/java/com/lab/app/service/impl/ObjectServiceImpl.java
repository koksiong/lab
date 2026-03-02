package com.lab.app.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lab.app.dao.ObjectRepository;
import com.lab.app.model.dto.DataObject;
import com.lab.app.model.entity.KeyValue;
import com.lab.app.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ObjectServiceImpl implements ObjectService {
    public static final ZoneId zoneId = ZoneId.of("UTC");

    private final Logger logger = LoggerFactory.getLogger(ObjectServiceImpl.class);
    private final ObjectRepository objectRepository;

    // local cache for coding test, can be expanded into REDIS if in PRD
    private final Cache<String, AtomicLong> versionCache = Caffeine.newBuilder()
            .maximumSize(10_000) // Evict if size exceeds 10,000
            .expireAfterWrite(1, TimeUnit.MINUTES) // Expire 1 min after write
            .recordStats() // Optional: enable statistics
            .build();

    public ObjectServiceImpl(ObjectRepository objectRepository) {
        this.objectRepository = objectRepository;
    }

    @Override
    @Transactional
    public List<DataObject> updateObjects(Map<String, String> inputs) {
        final List<DataObject> list = new ArrayList<>();

        inputs.forEach((key, value) -> {
            logger.info("updateObjects:: processing Key: {}", key);
            final AtomicLong version = versionCache.get(key, k -> {
                Optional<KeyValue> dbObject = objectRepository.findTopByKvKeyOrderByCreatedDesc(key);
                return dbObject.map(keyValue -> new AtomicLong(keyValue.getVersion()))
                        .orElseGet(() -> new AtomicLong(0L));
            });
            KeyValue kvEntity = new KeyValue();
            kvEntity.setVersion(version.incrementAndGet());
            kvEntity.setKvKey(key);
            kvEntity.setKvValue(value);
            kvEntity.setCreated(LocalDateTime.now(zoneId));

            final KeyValue save = objectRepository.save(kvEntity);
            DataObject dto = new DataObject();
            dto.setId(save.getId());
            dto.setKey(save.getKvKey());
            dto.setValue(save.getKvValue());
            dto.setVersion(save.getVersion());
            dto.setTimestamp(save.getCreated());
            list.add(dto);

        });
        return list;
    }

    @Override
    public String getLatestValue(String key, String timestamp) {
        logger.info("Getting latest object, using key: {}", key);
        if (timestamp != null) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(timestamp)), zoneId);
            logger.info("Timestamp: {}", localDateTime);
            Optional<KeyValue> db = objectRepository.findTopByKvKeyAndCreatedLessThanEqualOrderByCreatedDesc(key, localDateTime);
            return db.map(KeyValue::getKvValue).orElse(null);
        }
        return objectRepository.findTopByKvKeyOrderByCreatedDesc(key).map(KeyValue::getKvValue).orElse(null) ;

    }

    @Override
    public List<DataObject> getAllObjects() {
        return objectRepository.findAll().stream()
                .map(db -> {
                    DataObject dataObject = new DataObject();
                    dataObject.setId(db.getId());
                    dataObject.setKey(db.getKvKey());
                    dataObject.setValue(db.getKvValue());
                    dataObject.setTimestamp(db.getCreated());
                    dataObject.setVersion(db.getVersion());
                    return dataObject;
                }).toList();
    }

}
