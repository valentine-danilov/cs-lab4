package by.danilov.cs.lab2.util;

import by.danilov.cs.lab2.domain.FetchResponse;

import javax.servlet.http.HttpSession;

public final class AttributesUtil {

    private AttributesUtil() {

    }

    public static void setRequestIdAttribute(HttpSession session, FetchResponse response) {

        String requestId = RandomSequenceGeneration.generateRandomRequestKey();
        response.addAttribute("REQUEST_ID", requestId);
        session.setAttribute("REQUEST_ID", requestId);

    }

}
