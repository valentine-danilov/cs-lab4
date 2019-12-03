package by.danilov.cs.lab2.repository;

import by.danilov.cs.lab2.domain.User;
import by.danilov.cs.lab2.exception.UserNotExistException;
import by.danilov.cs.lab2.repository.store.UserStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Objects;

@Repository
//@Slf4j
public class UserRepository {

    private static final String JSON_PATH = "src\\main\\resources\\store\\users\\users.json";
    private UserStore userStore;

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            userStore = objectMapper.readValue(new File(JSON_PATH), UserStore.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User getByLogin(String login) throws UserNotExistException {
        return userStore.getUsers().stream()
                .filter(user -> Objects.equals(user.getLogin(), login))
                .findFirst()
                .orElseThrow(() ->
                        new UserNotExistException(
                                MessageFormat.format(
                                        "User with login '{0}' does not  exits",
                                        login)
                        )
                );
    }

}
