package com.clearblade.java.api;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MqttClientTests {

    @Test
    void subscribingToOneTopicRegistersOneCallback() {

        MqttClient client = spy(MqttClient.class);

        Whitebox
        Assertions.fail();
    }

    @Test
    void subscribingToTwoTopicsRegistersTwoCallbacks() {
        Assertions.fail();
    }

    @Test
    void resubscribingWithNoPreviousSubscribeResubscribesToNoTopics() {
        Assertions.fail();
    }

    @Test
    void resubscribingWithTwoPreviousSubscribeResubscribesToTwoTopics() {
        Assertions.fail();
    }

    @Test
    void resubscribingWithOnePreviousSubscribeResubscribesToOneTopic() {
        Assertions.fail();
    }

    @Test
    void unsubscribingFromTopicRemovesCallback() {
        Assertions.fail();
    }

    @Test
    void connectCompleteEventCallsOnConnectionComplete() {
        Assertions.fail();
    }

    @Test
    void connectionLostEventCallsOnConnectionLost() {
        Assertions.fail();
    }

    @Test
    void messageArrivedEventOnSubscribedTopicUsesCallback() {
        Assertions.fail();
    }

    @Test
    void messageArrivedEventOnUnknownTopicIgnoresMessage() {
        Assertions.fail();
    }

}
