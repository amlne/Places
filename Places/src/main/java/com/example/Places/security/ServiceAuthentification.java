package com.example.Places.security;

import com.example.Places.dao.DAOFactory;

public class ServiceAuthentification {

    public static boolean authenticate(String username, String password) {
        boolean authenticationStatus = "places".equals(username) && "places".equals(password);
        return DAOFactory.getAccountDAO().authentificate(username, password);
    }
}
