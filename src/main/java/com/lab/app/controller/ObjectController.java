package com.lab.app.controller;

import com.lab.app.model.dto.DataObject;
import com.lab.app.service.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/object")
public class ObjectController {
    private final Logger logger = LoggerFactory.getLogger(ObjectController.class);

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }


    /**
     * Creates or updates in according to giving inputs
     * @param inputs
     *      map of key and value to be updated/created
     * @return
     */
    @PostMapping
    public List<DataObject> postObject(@RequestBody Map<String, String> inputs) {
        if (Objects.isNull(inputs)) {
            throw new NullPointerException("inputs is empty");
        }
        return objectService.updateObjects(inputs);
    }

    /**
     * Returns the stored value using key and latest timestamp
     * @param key
     *      mandatory key
     * @param timestamp
     *      optional latest timestamp
     * @return
     */
    @GetMapping("/{key}")
    public String getObject(@PathVariable String key, @RequestParam(value = "timestamp", required = false) String timestamp) {
        if (Objects.isNull(key)) {
            throw new NullPointerException("key is empty");
        }
        return objectService.getLatestValue(key, timestamp);
    }

    /**
     * Returns all stored objects
     */
    @GetMapping("/get_all_records")
    public List<DataObject> getAllObjects() {
        return objectService.getAllObjects();
    }
}
