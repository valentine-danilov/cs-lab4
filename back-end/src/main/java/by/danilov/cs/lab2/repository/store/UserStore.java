package by.danilov.cs.lab2.repository.store;

import by.danilov.cs.lab2.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserStore {

    private List<User> users;

    public UserStore() {
    }
}
