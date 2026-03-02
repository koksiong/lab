package com.lab.app.service;

import com.lab.app.model.dto.DataObject;

import java.util.List;
import java.util.Map;

public interface ObjectService {
    List<DataObject> updateObjects(Map<String, String> inputs);

    String getLatestValue(String key, String timestamp);

    List<DataObject> getAllObjects();
}
