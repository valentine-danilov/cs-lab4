package by.danilov.cs.lab2.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerification {
    @JsonProperty("email")
    private String email;

    private String verificationCode;

    public EmailVerification() {
    }
}
