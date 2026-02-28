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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObjectServiceTest {

    @Mock
    private ObjectRepository objectRepository;

    @InjectMocks
    private ObjectService objectService;

    @Test
    public void testUpdateObjects_with_empty_inputs() {
        List<DataObject> result = objectService.updateObjects(new HashMap<>());
        assertThat(result).isEmpty();
    }

    @Test
    public void testUpdateObjects_with_no_db_object() {
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
    public void testUpdateObjects_with_db_object() {
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
    public void testGetLatestObjects_with_no_timestamp() {
        fail("Not yet implemented");
    }

}
