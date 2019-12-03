package by.danilov.cs.lab2.security.controller;

import by.danilov.cs.lab2.domain.FetchResponse;
import by.danilov.cs.lab2.domain.Key;
import by.danilov.cs.lab2.domain.Token;
import by.danilov.cs.lab2.service.RSATokenService;
import by.danilov.cs.lab2.util.AttributesUtil;
import by.danilov.cs.lab2.util.RandomSequenceGeneration;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth/token")
public class TokenController {

    private final RSATokenService tokenService;

    private final RSATokenService rsaTokenService;

    public TokenController(RSATokenService tokenService, RSATokenService rsaTokenService) {
        this.tokenService = tokenService;
        this.rsaTokenService = rsaTokenService;
    }

    @PostMapping(
            value = "/request",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    private ResponseEntity<FetchResponse> requestToken(@RequestBody Key publicKey,
                                                       HttpServletRequest request) throws Exception {

        HttpSession httpSession = request.getSession(true);

        Token token = rsaTokenService.generateSessionToken();
        Token encryptedToken = tokenService.getEncryptedToken(publicKey.getKey(), token);

        httpSession.setAttribute("token", token.getToken());
        httpSession.setAttribute("iv", token.getIv());

        FetchResponse fetchResponse = new FetchResponse();
        fetchResponse.addAttribute("token", encryptedToken.getToken());
        fetchResponse.addAttribute("iv", encryptedToken.getIv());
        AttributesUtil.setRequestIdAttribute(httpSession, fetchResponse);

        return ResponseEntity.ok(fetchResponse);
    }
}
