package com.clearblade.java.api.auth;

import java.util.Map;

public interface Auth {
    // doAuth attempts to authenticate using the underlying Auth method.
    void doAuth() throws AuthException;

    // check attempts to check authentication using the underlying Auth method.
    void doCheck() throws AuthException;

    // doLogout logs out from the underlying Auth method.
    void doLogout() throws AuthException;

    // isAuthed returns whenever the authentication was successful.
    boolean isAuthed();

    // token returns the Auth token for this Auth method.
    String getToken();

    // requestHeaders returns important headers derived from the Auth method.
    Map<String, String> getRequestHeaders();
}
