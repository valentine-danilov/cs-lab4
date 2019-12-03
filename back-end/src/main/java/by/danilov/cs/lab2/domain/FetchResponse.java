package by.danilov.cs.lab2.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
public class FetchResponse {

    private Map<String, Object> attributes;

    public FetchResponse() {
    }

    public void addAttribute(String key, Object value) {

        if (isNull(attributes)) {
            attributes = new HashMap<>();
        }

        attributes.put(key, value);
    }
}
