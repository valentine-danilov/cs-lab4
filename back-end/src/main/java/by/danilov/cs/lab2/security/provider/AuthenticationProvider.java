package by.danilov.cs.lab2.security.provider;

import by.danilov.cs.lab2.domain.User;
import by.danilov.cs.lab2.exception.UserNotExistException;
import by.danilov.cs.lab2.repository.UserRepository;
import by.danilov.cs.lab2.security.details.UserDetailsImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationProvider {

    private final UserRepository repository;

    public AuthenticationProvider(UserRepository repository) {
        this.repository = repository;
    }

    public boolean authorize(UserDetailsImpl userDetails) {

        try {
            User user = repository.getByLogin(userDetails.getLogin());
            String hashedPassword = hashPasswordMD5(userDetails.getPassword());
            return Objects.equals(user.getPassword(), hashedPassword);

        } catch (UserNotExistException e) {
            return false;
        }
    }

    private String hashPasswordMD5(String password) {
        return DigestUtils.md5Hex(password).toUpperCase();
    }

}
