package by.danilov.cs.lab2.security.controller;

import by.danilov.cs.lab2.domain.EmailVerification;
import by.danilov.cs.lab2.domain.FetchResponse;
import by.danilov.cs.lab2.exception.BadRequestException;
import by.danilov.cs.lab2.security.details.UserDetailsImpl;
import by.danilov.cs.lab2.security.provider.AuthenticationProvider;
import by.danilov.cs.lab2.service.AESService;
import by.danilov.cs.lab2.service.EmailServiceImpl;
import by.danilov.cs.lab2.util.RandomSequenceGeneration;
import by.danilov.cs.lab2.util.Verifier;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;

import static by.danilov.cs.lab2.util.ResponseBuilder.buildFetchResponse;
import static by.danilov.cs.lab2.util.Verifier.isValidVerificationCode;
import static java.util.Objects.isNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AuthorizationController {

    private final AESService aesService;
    private final AuthenticationProvider authProvider;
    private final EmailServiceImpl emailService;

    public AuthorizationController(AESService aesService,
                                   AuthenticationProvider authProvider, EmailServiceImpl emailService) {
        this.aesService = aesService;
        this.authProvider = authProvider;
        this.emailService = emailService;
    }

    @PostMapping(value = "/two_factor/code-validation")
    public ResponseEntity validateEMailCode(@RequestBody EmailVerification emailVerification, HttpServletRequest request) {

        if (!Verifier.verify(request)) {
            return ResponseEntity.badRequest().build();
        }
        HttpSession session = request.getSession(false);
        String encryptedCode = emailVerification.getVerificationCode();
        String frontendEmailVerificationCode = null;

        try {
            frontendEmailVerificationCode = decrypt(encryptedCode, session);
        } catch (BadRequestException e) {
            return buildFetchResponse("Verification code is invalid",
                    HttpStatus.BAD_REQUEST,
                    session,
                    false);
        }

        String backendEmailVerificationCode = (String) session.getAttribute("EMAIL_VERIFICATION_CODE");

        if (isValidVerificationCode(backendEmailVerificationCode, frontendEmailVerificationCode)) {
            return buildFetchResponse("Verification code is invalid",
                    HttpStatus.BAD_REQUEST,
                    session,
                    false);
        } else {
            return buildFetchResponse("Verified",
                    HttpStatus.OK,
                    session,
                    true);
        }
    }

    @PostMapping(value = "/two_factor/email-verification")
    public ResponseEntity<FetchResponse> getEmail(@RequestBody EmailVerification emailVerification,
                                                  HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (!Verifier.verify(request)) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String emailTo = decrypt(emailVerification.getEmail(), session);
            String subject = "Authentication verification";
            String verificationKey = RandomSequenceGeneration.generateRandomNumber(6);
            String text = MessageFormat.format("Your code: {0}", verificationKey);
            emailService.sendSimpleMessage(emailTo, subject, text);
            session.setAttribute("EMAIL_VERIFICATION_CODE", verificationKey);
            return buildFetchResponse(
                    "E-Mail was sent successfully",
                    HttpStatus.OK, session, true);

        } catch (Exception e) {
            return buildFetchResponse(
                    e.getMessage(),
                    HttpStatus.BAD_REQUEST, session, false);
        }


    }

    @PostMapping(value = "/auth")
    public ResponseEntity<FetchResponse> authorize(@RequestBody UserDetailsImpl encryptedDetails,
                                                   HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            if (!Verifier.verify(request)) {
                return ResponseEntity.badRequest().build();
            }

            UserDetailsImpl decryptedDetails = decryptDetails(encryptedDetails, session);
            boolean authenticationResult = authProvider.authorize(decryptedDetails);
            session.setAttribute("USER_ID", DigestUtils.md5Hex(decryptedDetails.getLogin()));
            return getResponse(authenticationResult, request);

        } catch (BadRequestException e) {
            return buildFetchResponse(
                    e.getMessage(), HttpStatus.BAD_REQUEST,
                    null, false);
        }
    }

    private ResponseEntity<FetchResponse> getResponse(boolean authenticationResult,
                                                      HttpServletRequest request) {
        if (authenticationResult) {
            HttpSession session = request.getSession(false);
            if (isNull(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return buildFetchResponse(
                    "Authentication successful",
                    HttpStatus.OK, session, true);

        } else {
            return buildFetchResponse(
                    "Invalid login or password",
                    HttpStatus.UNAUTHORIZED, null, false);
        }
    }

    private UserDetailsImpl decryptDetails(UserDetailsImpl encryptedDetails, HttpSession session) throws BadRequestException {
        String decryptedLogin = decrypt(encryptedDetails.getLogin(), session);
        String decryptedPassword = decrypt(encryptedDetails.getPassword(), session);
        return new UserDetailsImpl(decryptedLogin, decryptedPassword);
    }

    private String decrypt(String encryptedText, HttpSession session) throws BadRequestException {
        try {
            String key = (String) session.getAttribute("token");
            String iv = (String) session.getAttribute("iv");
            return aesService.decryptAES_CBC(encryptedText, key, iv);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }
}
