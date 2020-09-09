package com.clearblade.examples;

import com.clearblade.java.api.*;
import com.clearblade.java.api.auth.UserAuth;

public class UserAuthExample {

    public static InitOptions makeInitOptions() {

        String platformUrl = System.getenv("PLATFORM_URL");
        String messagingUrl = System.getenv("MESSAGING_URL");
        String email = System.getenv("EMAIL");
        String password = System.getenv("PASSWORD");

        InitOptions options = new InitOptions();

        if (platformUrl != null && platformUrl.length() > 0) {
            System.out.println("Found PLATFORM_URL");
            options.setPlatformUrl(platformUrl);
        }

        if (messagingUrl != null && messagingUrl.length() > 0) {
            System.out.println("Found MESSAGING_URL");
            options.setMessagingUrl(messagingUrl);
        }

        options.setAuth(new UserAuth(email, password));
        return options;
    }

    public static void main(String[] args) throws InterruptedException {

        String systemKey = System.getenv("SYSTEM_KEY");
        String systemSecret = System.getenv("SYSTEM_SECRET");

        InitCallback initCb = new InitCallback() {
            @Override
            public void done(boolean results) {
                System.out.print("done connecting.\n");
            }

            @Override
            public void error(ClearBladeException exception) {
                System.err.printf("error connecting: %s\n", exception);
                exception.getCause().printStackTrace();
            }
        };

        InitOptions options = makeInitOptions();
        ClearBlade.initialize(systemKey, systemSecret, options, initCb);

        System.out.println("Publishing...");
        MQTTClient mqttClient = new MQTTClient("UserClientID", 1);
        for (int idx = 0; idx < 100; idx++) {
            mqttClient.publish("userMessagingTopic", String.format("userMessagingBody %s", idx));
        }
        mqttClient.disconnect();
    }
}
