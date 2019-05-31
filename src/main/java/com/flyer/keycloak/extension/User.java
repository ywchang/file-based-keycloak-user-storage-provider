package com.flyer.keycloak.extension;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Map<String, List<String>> attributes = new HashMap<>();

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String firstName, String lastName, String email) {
        this(username);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = "";
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }
}
