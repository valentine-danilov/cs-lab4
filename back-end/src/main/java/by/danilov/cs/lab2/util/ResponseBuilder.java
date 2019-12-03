package by.danilov.cs.lab2.util;

import by.danilov.cs.lab2.domain.FetchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

public final class ResponseBuilder {

    private static final String REQUEST_ID_KEY = "REQUEST_ID";
    private static final String RESPONSE_CODE_KEY = "code";
    private static final String RESPONSE_MESSAGE_KEY = "message";

    private ResponseBuilder() {
    }

    public static ResponseEntity<FetchResponse> buildFetchResponse(String message,
                                                                   HttpStatus status,
                                                                   HttpSession session,
                                                                   boolean refreshRequestId) {

        FetchResponse response = new FetchResponse();

        if (refreshRequestId) {
            String requestId = RandomSequenceGeneration.generateRandomRequestKey();
            response.addAttribute("REQUEST_ID", requestId);
            session.setAttribute("REQUEST_ID", requestId);
        }

        response.addAttribute(RESPONSE_CODE_KEY, status.value());
        response.addAttribute(RESPONSE_MESSAGE_KEY, message);

        return ResponseEntity.status(status).body(response);
    }
}
