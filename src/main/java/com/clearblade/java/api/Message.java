package com.clearblade.java.api;

/**
 * Message class is kept for backwards compatibility. It's currently being
 * replaced by {@link com.clearblade.java.api.MQTTClient}. It uses delegation on
 * the constructors since they are not automatically inherited.
 *
 * @deprecated use {@link com.clearblade.java.api.MQTTClient} instead.
 */
@Deprecated
public class Message extends MQTTClient {

    public Message(String clientID) {
        super(clientID);
    }

    public Message(String clientID, int qos) {
        super(clientID, qos);
    }
}
