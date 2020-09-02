package com.clearblade.java.api.auth;

import java.util.Map;

public class AnonAuth implements Auth {

    public void auth() throws AuthException {}

    public void check() throws AuthException {}

    public boolean isAuthed() {
        return false;
    }

    public String token() {
        return "";
    }

    public Map<String, String> requestHeaders() {
        return null;
    }
}
