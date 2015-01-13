/**
 * OAuthPasswordCredentials.java class file
 */
package com.secucard.connect.auth;

/**
 * Password credentials used for authorization
 */
public class UserCredentials {
    private final String username;
    private final String password;

    /**
     * Constructor
     *
     * @param username
     * @param password
     */
    public UserCredentials(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /**
     * Getters
     */
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}