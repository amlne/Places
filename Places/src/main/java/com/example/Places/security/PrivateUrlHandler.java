package com.example.Places.security;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PrivateUrlHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic")) {
            String[] up = parseBasic(authHeader.substring(authHeader.indexOf(" ") + 1));
            String username = up[0];
            String password = up[1];

            if (ServiceAuthentification.authenticate(username, password))
                return;
            else {
                httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Please provide username and password");
                return;
            }
        }
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Please provide username and password");
    }

    private String[] parseBasic(String enc) {
        byte[] bytes = Base64.decodeBase64(enc.getBytes());
        String s = new String(bytes);
        int pos = s.indexOf(":");

        if (pos >= 0)
            return new String[]{s.substring(0, pos), s.substring(pos + 1)};
        else
            return new String[]{s, null};
    }
}
