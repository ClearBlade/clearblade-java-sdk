package com.clearblade.examples;

import com.clearblade.java.api.*;
import com.clearblade.java.api.auth.DeviceAuth;

public class DeviceAuthExample {

    public static InitOptions makeInitOptions() {

        String platformUrl = System.getenv("PLATFORM_URL");
        String messagingUrl = System.getenv("MESSAGING_URL");

        String systemKey = System.getenv("SYSTEM_KEY");
        String deviceName = System.getenv("DEVICE_NAME");
        String activeKey = System.getenv("ACTIVE_KEY");

        InitOptions options = new InitOptions();

        if (platformUrl != null && platformUrl.length() > 0) {
            System.out.println("Found PLATFORM_URL");
            options.setPlatformUrl(platformUrl);
        }

        if (messagingUrl != null && messagingUrl.length() > 0) {
            System.out.println("Found MESSAGING_URL");
            options.setMessagingUrl(messagingUrl);
        }

        options.setAuth(new DeviceAuth(systemKey, deviceName, activeKey));
        return options;
    }

    public static void initializeClearBlade() {

        String systemKey = System.getenv("SYSTEM_KEY");
        String systemSecret = System.getenv("SYSTEM_SECRET");

        InitCallback initCb = new InitCallback() {
            @Override
            public void done(boolean results) {
                System.out.print("done initializing\n");
            }

            @Override
            public void error(ClearBladeException exception) {
                System.err.printf("error initializing: %s\n", exception);
                exception.getCause().printStackTrace();
            }
        };

        InitOptions options = makeInitOptions();
        ClearBlade.initialize(systemKey, systemSecret, options, initCb);
    }

    public static void main(String[] args) throws InterruptedException, ClearBladeException {

        initializeClearBlade();

        String identifier = "deviceClientID";
        String firstTopic = "firstDeviceMessagingTopic";
        String secondTopic = "secondDeviceMessagingTopic";

        MqttClient mqttClient = new MqttClient(identifier, 1);

        mqttClient.subscribe(firstTopic, new MessageCallback() {
            @Override
            public void done(String topic, String message){
                System.out.printf("first received %s: %s%n", topic, message);
            }
        });

        mqttClient.subscribe(secondTopic, new MessageCallback() {
            @Override
            public void done(String topic, String message) {
                System.out.printf("second received %s: %s%n", topic, message);
            }
        });

        System.out.println("Publishing...");

        for (int idx = 0; idx < 100; idx++) {

            try {
                mqttClient.publish(firstTopic, String.format("messageBody %d", idx));
                mqttClient.publish(secondTopic, String.format("messageBody %d", idx));

            } catch (ClearBladeException e) {
                System.out.println(e.getMessage());
            }

            Thread.sleep(1000);
        }

        mqttClient.disconnect();
    }
}
