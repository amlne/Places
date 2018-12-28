package com.example.Places.res;

public class AccountResource {
    private String username; // Clé primaire, unique. On l'utilise pour les requêtes.
    private String email; // Check lors de l'inscription.
    private String password;
    private String followers; // liste 1 jai autoriser
    private String subscriptions; // liste 2 ils mont autoriser
    private String EsId;


    public AccountResource() {
        super();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(String subscriptions) {
        this.subscriptions = subscriptions;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEsId() {
        return EsId;
    }

    public void setEsId(String esId) {
        EsId = esId;
    }
}