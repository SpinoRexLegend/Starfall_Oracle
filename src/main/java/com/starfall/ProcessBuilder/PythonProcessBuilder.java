package com.starfall.ProcessBuilder;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PythonProcessBuilder {

    public HashMap<String, Object> buildRequest(Integer diameter, Integer velocity) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("Diameter", diameter);
        map.put("Velocity", velocity);

        return map;
    }
}