package com.clearblade.java.api.auth;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class UserAuth implements Auth {

    private String _email;
    private String _password;
    private String _token;

    public UserAuth(String email, String password) {
        this._email = email;
        this._password = password;
        this._token = null;
    }

    synchronized public void auth() throws AuthException {

        JsonObject payload = new JsonObject();
        payload.addProperty("email", this._email);
        payload.addProperty("password", this._password);

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v1/user/auth")
                .body(payload)
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse result = request.execute();
        if (result.getError()) {
            throw new AuthException("unable to authenticate using user");
        }

        JsonObject obj = (JsonObject) JsonParser.parseString((String) result.getData());
        String authToken = obj.get("user_token").getAsString();

        this._token = authToken;
    }

    public void check() throws AuthException {

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v1/user/checkauth")
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse result = request.execute();
        if (result.getError()) {
            throw new AuthException("unable to check user authentication");
        }

        JsonObject obj = (JsonObject) JsonParser.parseString((String) result.getData());
        boolean isAuthenticated = obj.get("is_authenticated").getAsString().equalsIgnoreCase("true");
        if (!isAuthenticated) {
            throw new AuthException("user is not authenticated");
        }
    }

    public boolean isAuthed() {
        return this._token != null && this._token.length() > 0;
    }

    public String token() {
        return this._token == null ? "" : this._token;
    }

    public Map<String, String> requestHeaders() {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("ClearBlade-UserToken", this.token());
        return m;
    }
}
