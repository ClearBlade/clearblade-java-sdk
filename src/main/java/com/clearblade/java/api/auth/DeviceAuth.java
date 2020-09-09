package com.clearblade.java.api.auth;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class DeviceAuth implements Auth {

    private final String _systemKey;
    private final String _deviceName;
    private final String _activeKey;
    private String _token;

    public DeviceAuth(String systemKey, String deviceName, String activeKey) {
        this._systemKey = systemKey;
        this._deviceName = deviceName;
        this._activeKey = activeKey;
        this._token = null;
    }

    /**
     * Authenticates against the ClearBlade API V2 for devices.
     * see: https://docs.clearblade.com/v/4/static/api/index.html#/Device/AuthDevice
     */
    public void doAuth() throws AuthException {

        boolean systemKeyMissing = this._systemKey == null || this._systemKey.length() <= 0;
        boolean deviceNameMissing = this._deviceName == null || this._deviceName.length() <= 0;
        boolean activeKeyMissing = this._activeKey == null || this._activeKey.length() <= 0;

        if (systemKeyMissing || deviceNameMissing || activeKeyMissing) {
            throw new AuthException("DeviceAuth needs all system key, device name, and active key");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("deviceName", this._deviceName);
        payload.addProperty("activeKey", this._activeKey);

        String requestPath = String.format("api/v/2/devices/%s/auth", this._systemKey);

        RequestProperties headers = new RequestProperties
                .Builder()
                .method("POST")
                .endPoint(requestPath)
                .body(payload)
                .build();

        RequestEngine request = new RequestEngine();
        request.setHeaders(headers);

        PlatformResponse<String> result = request.execute();
        if (result.isError()) {
            throw new AuthException(String.format("unable to authenticate device: %s", result.getData()));
        }

        JsonObject obj = (JsonObject) JsonParser.parseString(result.getData());
        this._token = obj.get("deviceToken").getAsString();
    }

    public void doCheck() throws AuthException {
        if (!this.isAuthed()) {
            throw new AuthException("unable to check login for device");
        }
    }

    public void doLogout() throws AuthException {
        if (!this.isAuthed()) {
            throw new AuthException(("unable to logout device when not authenticated"));
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
        m.put("ClearBlade-DeviceToken", this.getToken());
        return m;
    }
}
