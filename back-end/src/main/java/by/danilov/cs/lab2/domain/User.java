package by.danilov.cs.lab2.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String login;
    private String password;

    public User() {
    }
}
