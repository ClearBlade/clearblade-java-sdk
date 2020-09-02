package com.clearblade.examples;
import com.clearblade.java.api.*;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static HashMap<String, Object> makeInitOptions() {

        String platformURL = System.getenv("PLATFORM_URL");
        String messagingURL = System.getenv("MESSAGING_URL");
        String email = System.getenv("EMAIL");
        String password = System.getenv("PASSWORD");

        HashMap<String, Object> options = new HashMap<String, Object>();

        if (platformURL != null && platformURL.length() > 0) {
            System.out.println("Found PLATFORM_URL");
            options.put("platformURL", platformURL);
        }

        if (messagingURL != null && messagingURL.length() > 0) {
            System.out.println("Found MESSAGING_URL");
            options.put("messagingURL", messagingURL);
        }

        if (email != null && email.length() > 0) {
            System.out.println("Found EMAIL");
            options.put("email", email);
        }

        if (password != null && password.length() > 0) {
            System.out.println("Found PASSWORD");
            options.put("password", password);
        }

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
            }
        };

        HashMap<String, Object> options = makeInitOptions();
        ClearBlade.initialize(systemKey, systemSecret, options, initCb);

        System.out.println("Publishing...");
        MQTTClient mqttClient = new MQTTClient("TestClientID", 1);
        for (int idx = 0; idx < 1000; idx++) {
            mqttClient.publish("messagingTopic", "messagingStuff");
        }
    }
}
