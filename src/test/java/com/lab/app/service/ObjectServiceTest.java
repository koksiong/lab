package com.lab.app.service;

import com.lab.app.dao.ObjectRepository;
import com.lab.app.model.dto.DataObject;
import com.lab.app.model.entity.KeyValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.lab.app.service.ObjectService.zoneId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjectServiceTest {

    @Mock
    private ObjectRepository objectRepository;

    @InjectMocks
    private ObjectService objectService;

    @Test
    void testUpdateObjects_with_empty_inputs() {
        List<DataObject> result = objectService.updateObjects(new HashMap<>());
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateObjects_with_no_db_object() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("id", "1");
        Optional<KeyValue> dbObject = Optional.empty();
        when(objectRepository.findTopByKvKeyOrderByCreatedDesc("id")).thenReturn(dbObject);
        when(objectRepository.save(any(KeyValue.class))).then(AdditionalAnswers.returnsFirstArg());

        List<DataObject> result = objectService.updateObjects(inputs);

        verify(objectRepository).findTopByKvKeyOrderByCreatedDesc("id");
        verify(objectRepository).save(any(KeyValue.class));
        assertThat(result).hasSize(1);
        DataObject dataObject = result.get(0);
        assertThat(dataObject.getKey()).isEqualTo("id");
        assertThat(dataObject.getValue()).isEqualTo("1");
        assertThat(dataObject.getVersion()).isEqualTo(1);
        assertThat(dataObject.getTimestamp()).isNotNull();
    }

    @Test
    void testUpdateObjects_with_db_object() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("id", "1");
        KeyValue dbKeyValue = new KeyValue();
        dbKeyValue.setVersion(5L);
        dbKeyValue.setId(1000L);
        dbKeyValue.setKvKey("id");
        dbKeyValue.setKvValue("1000");
        Optional<KeyValue> dbObject = Optional.of(dbKeyValue);
        when(objectRepository.findTopByKvKeyOrderByCreatedDesc("id")).thenReturn(dbObject);
        when(objectRepository.save(any(KeyValue.class))).then(AdditionalAnswers.returnsFirstArg());

        List<DataObject> result = objectService.updateObjects(inputs);

        verify(objectRepository).findTopByKvKeyOrderByCreatedDesc("id");
        verify(objectRepository).save(any(KeyValue.class));
        assertThat(result).hasSize(1);
        DataObject dataObject = result.get(0);
        assertThat(dataObject.getKey()).isEqualTo("id");
        assertThat(dataObject.getValue()).isEqualTo("1");
        assertThat(dataObject.getVersion()).isEqualTo(6L);
        assertThat(dataObject.getTimestamp()).isNotNull();
    }

    @Test
    void testGetLatestObjects_with_no_timestamp() {
        when(objectRepository.findTopByKvKeyOrderByCreatedDesc("id")).thenReturn(Optional.empty());

        final String result = objectService.getLatestValue("id", null);

        verify(objectRepository).findTopByKvKeyOrderByCreatedDesc("id");
        assertThat(result).isNull();
    }

    @Test
    void testGetLatestObjects_with_timestamp() {
        KeyValue value = new KeyValue();
        value.setKvKey("id");
        value.setKvValue("1000");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong("1772242423")), zoneId);
        when(objectRepository.findTopByKvKeyAndCreatedLessThanEqualOrderByCreatedDesc("id", localDateTime)).thenReturn(Optional.of(value));

        final String result = objectService.getLatestValue("id", "1772242423");

        verify(objectRepository).findTopByKvKeyAndCreatedLessThanEqualOrderByCreatedDesc("id", localDateTime);
        assertThat(result).isEqualTo("1000");
    }

    @Test
    void testGetAllObjects() {
        KeyValue kValue = new KeyValue();
        kValue.setKvKey("id");
        kValue.setKvValue("1000");
        when(objectRepository.findAll()).thenReturn(List.of(kValue));

        final List<DataObject> result = objectService.getAllObjects();

        verify(objectRepository).findAll();
        assertThat(result).hasSize(1);
        DataObject dataObject = result.get(0);
        assertThat(dataObject.getKey()).isEqualTo("id");
        assertThat(dataObject.getValue()).isEqualTo("1000");
    }

}
