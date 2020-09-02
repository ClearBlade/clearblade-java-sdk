package com.clearblade.java.api.auth;

import java.util.Map;

public class AnonAuth implements Auth {

    public void doAuth() throws AuthException {}

    public void doCheck() throws AuthException {}

    public void doLogout() throws AuthException {}

    public boolean isAuthed() {
        return false;
    }

    public String getToken() {
        return "";
    }

    public Map<String, String> getRequestHeaders() {
        return null;
    }
}
