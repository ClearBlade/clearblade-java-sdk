package com.clearblade.java.api.auth;

import java.util.Map;

public interface Auth {
    // auth attempts to authenticate using the underlying Auth method.
    void auth() throws AuthException;

    // check attempts to check authentication using the underlying Auth method.
    void check() throws AuthException;

    // isAuthed returns whenever the authentication was successful.
    boolean isAuthed();

    // token returns the Auth token for this Auth method.
    String token();

    // requestHeaders returns important headers derived from the Auth method.
    Map<String, String> requestHeaders();
}
