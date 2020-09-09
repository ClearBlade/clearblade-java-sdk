package com.clearblade.java.api.auth;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AnonAuth extends UserAuth {

    public AnonAuth() {
        super("", "");
    }

    @Override
    public synchronized void doAuth() throws AuthException {

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint("api/v/1/user/anon")
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse<String> result = request.execute();
        if (result.isError()) {
            throw new AuthException(String.format("unable to authenticate anonymous user: %s", result.getData()));
        }

        JsonObject obj = (JsonObject) JsonParser.parseString(result.getData());
        this._token = obj.get("user_token").getAsString();
    }
}
