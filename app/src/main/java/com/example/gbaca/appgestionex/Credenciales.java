package com.example.gbaca.appgestionex;

/**
 * Created by GBaca on 7/7/2017.
 */

public class Credenciales {

    private String user;
    private String password;

    public Credenciales(String usuario, String password) {
        user = usuario;
        this.password = password;
    }

    public Credenciales() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
