package com.clearblade.java.api.auth;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class UserAuth implements Auth {

    private final String _email;
    private final String _password;
    private final boolean _tryRegister;
    protected String _token;

    public UserAuth(String email, String password) {
        this._email = email;
        this._password = password;
        this._tryRegister = false;
        this._token = null;
    }

    public UserAuth(String email, String password, boolean tryRegister) {
        this._email = email;
        this._password = password;
        this._tryRegister = tryRegister;
        this._token = null;
    }

    synchronized public void doAuth() throws AuthException {

        boolean emailMissing = this._email == null || this._email.length() <= 0;
        boolean passwordMissing = this._password == null || this._password.length() <= 0;

        if (emailMissing || passwordMissing) {
            throw new AuthException("UserAuth needs both email and password");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("email", this._email);
        payload.addProperty("password", this._password);

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v/1/user/auth")
                .body(payload)
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse<String> result = request.execute();
        if (result.isError()) {
            throw new AuthException(String.format("unable to authenticate user: %s", result.getData()));
        }

        JsonObject obj = (JsonObject) JsonParser.parseString(result.getData());
        this._token = obj.get("user_token").getAsString();
    }

    synchronized public void doCheck() throws AuthException {

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v/1/user/checkauth")
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse<String> result = request.execute();
        if (result.isError()) {
            throw new AuthException(String.format("unable check user authentication: %s", result.getData()));
        }

        JsonObject obj = (JsonObject) JsonParser.parseString(result.getData());
        boolean isAuthenticated = obj.get("is_authenticated").getAsString().equalsIgnoreCase("true");
        if (!isAuthenticated) {
            throw new AuthException("user is not authenticated");
        }
    }

    synchronized public void doLogout() throws AuthException {

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v/1/user/logout")
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse<String> result = request.execute();
        if (result.isError()) {
            throw new AuthException(String.format("unable to logout user: %s", result.getData()));
        }

        this._token = null;
    }

    public boolean isAuthed() {
        return this._token != null && this._token.length() > 0;
    }

    public String getToken() {
        return this._token == null ? "" : this._token;
    }

    public Map<String, String> getRequestHeaders() {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put("ClearBlade-UserToken", this.getToken());
        return m;
    }
}
