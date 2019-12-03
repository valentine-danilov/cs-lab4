package by.danilov.cs.lab2.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

import static java.util.Objects.isNull;

public final class Verifier {

    private Verifier() {
    }

    public static boolean verify(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (isNull(session)) {
            return false;
        }

        String backendRequestId = (String) session.getAttribute("REQUEST_ID");
        String frontendRequestId = request.getHeader("REQUEST_ID");

        return (!isNull(backendRequestId) && !isNull(frontendRequestId))
                || Objects.equals(backendRequestId, frontendRequestId);
    }

    public static boolean isValidVerificationCode(String backendEmailVerificationCode,
                                                  String frontendEmailVerificationCode) {
        return (!isNull(backendEmailVerificationCode) && !isNull(frontendEmailVerificationCode)
                && !Objects.equals(backendEmailVerificationCode, frontendEmailVerificationCode));
    }
}
