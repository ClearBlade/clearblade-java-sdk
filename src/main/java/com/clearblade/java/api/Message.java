package com.clearblade.java.api;

/**
 * Message class is kept for backwards compatibility. Uses delegation on
 * constructors since they are not inherited.
 *
 * @deprecated use {@link MqttClient} instead.
 */
@Deprecated
public class Message extends MqttClient {

    public Message(String clientID) throws ClearBladeException {
        super(clientID);
    }

    public Message(String clientID, int qos) throws ClearBladeException {
        super(clientID, qos);
    }
}
