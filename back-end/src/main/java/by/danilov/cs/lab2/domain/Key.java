package by.danilov.cs.lab2.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Key {

    public Key() {
    }

    public Key(String key) {
        this.key = key;
    }

    private String key;
}
