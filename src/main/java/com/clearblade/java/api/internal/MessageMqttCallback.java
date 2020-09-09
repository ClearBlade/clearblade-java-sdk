package com.clearblade.java.api.internal;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MessageMqttCallback implements MqttCallback {

	public void connectionLost(Throwable arg0) {
		System.out.println("received a connectionLost");
	}

	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		System.out.println("received a messageArrived");
	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		System.out.println("received a deliveryComplete");
	}
}
