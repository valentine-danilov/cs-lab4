package by.danilov.cs.lab2.security.store;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class KeyStore {

    private String token;
    private String iv;
    private String publicKey;
}
