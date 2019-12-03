package by.danilov.cs.lab2.security.details;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@JsonInclude
public class UserDetailsImpl {

    private String login;
    private String password;

    public UserDetailsImpl() {
    }

    public UserDetailsImpl(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
