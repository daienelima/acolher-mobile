package br.com.acolher.model;

public class Token {
    private String token;
    private String id;
    private boolean online;

    public boolean isOnline() {return online;}
    public void setOnline(boolean online) {this.online = online;}
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
