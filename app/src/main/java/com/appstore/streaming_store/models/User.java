package com.appstore.streaming_store.models;

public class User {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String document;
    private String password;
    private double saldo;
    private String token;

    public User(){}

    public User(String name, String email, String phone, String document, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.password = password;
        this.saldo = 1000000.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

