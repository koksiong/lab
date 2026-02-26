package com.lab.app.controller;

import com.lab.app.model.dto.DataObject;
import com.lab.app.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/object")
public class ObjectController {
    private final Logger logger = LoggerFactory.getLogger(ObjectController.class);

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @PostMapping
    public List<DataObject> postObject(@RequestBody Map<String, String> inputs) {
        return objectService.postObjects(inputs);
    }

    @GetMapping("/{key}")
    public String getObject(@PathVariable String key, @RequestParam(value = "timestamp", required = false) String timestamp) {
        logger.info("Getting object for key: " + key + ", timestamp: " + timestamp);
        return objectService.getLatestValue(key, timestamp);
    }

    @GetMapping("/get_all_records")
    public List<DataObject> getAllObjects() {
        return objectService.getAllObjects();
    }
}
