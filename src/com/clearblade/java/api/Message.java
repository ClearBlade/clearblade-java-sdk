package com.clearblade.java.api;

import java.util.HashSet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttCallback;

public class Message implements MqttCallback {
	
	public String url = ClearBlade.getMessageUrl();
	private static boolean isStarted = false; 
	HashSet<String> subscribed;
	int qualityOfService;
	String clientIdentifier;
	
	private static MemoryPersistence memoryPersistance; 		
	private static MqttConnectOptions opts;			

	private static MqttClient mqttClient;
	
	private MessageCallback messageReceivedCallback;
	
	
	public Message(String clientID) {
		
		clientIdentifier = clientID;
		qualityOfService = 0;
		connectToMQTTService();
		subscribed = new HashSet<String>();
	}
	
	
	public Message(String clientID, int qos) {
		
		clientIdentifier = clientID;
		qualityOfService = qos;
		connectToMQTTService();
		subscribed = new HashSet<String>();
	}
	
	
	/* First this method should be called in order to connect to the MQTT service. This method checks for the auth token and if its null,
	 * it terminates the connection attempt. If an auth token is available, it attempts to connect.
	 */
	public void connectToMQTTService() {
		
		if (isStarted)
			return;
		
		opts = new MqttConnectOptions();
		opts.setCleanSession(true);
		User curUser = ClearBlade.getCurrentUser(); 
		
		if (curUser.getAuthToken() == null) {
			
			System.out.println("Auth token is null");
			return;
		}
		
		else {
			
			opts.setUserName(curUser.getAuthToken());
			opts.setPassword(Util.getSystemKey().toCharArray());
		}
		
		connect();
		
	}
	
	public void connect() {
		
		try {
			
			mqttClient = new MqttClient(url, clientIdentifier, memoryPersistance);
			mqttClient.connect(opts);
			mqttClient.setCallback(this);
			isStarted = true;
			System.out.println("Connected to the MQTT Service");
		}
		
		catch(MqttException e) {
			
			System.out.println("Catching exception for MQTT connect");
			e.printStackTrace();
		}
		
	}
	
	public boolean disconnect() {
		
		if (!isStarted)
			return true;
		
		if (mqttClient != null) {
			
			try {
				
				mqttClient.disconnect();
			}
			
			catch(MqttException e) {
				
				e.printStackTrace();
			}
			
			mqttClient = null;
			isStarted = false;
			System.out.println("Disconnected from the MQTT Service");
			return true;
		}
		
		else {
			
			System.out.println("Unable to Disconnect");
			return false;
		}
	}
	
	
	public void publish (String topic, String message) {
		
		publish(topic, message.getBytes(), qualityOfService);
	}
	
	
	public void publish(String topic, byte[] payload, int qos) {
		
		try {
			
			mqttClient.publish(topic, payload, qos, false);
			System.out.println("Message published");
		} 
		
		catch (MqttPersistenceException e) {
			
			e.printStackTrace();
		} 
		
		catch (MqttException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void subscribe(String topic, MessageCallback callback) {
		
		if (subscribed.contains(topic))
			return;

		else {
			
			subscribed.add(topic);
		}
		
		subscribe(topic, qualityOfService, callback);
	}
	
	
	public void subscribe(String topic, int qos, MessageCallback callback) {
		
		try {
			
			mqttClient.subscribe(topic, qos);
			System.out.println("Subscribed");
			messageReceivedCallback = callback;
		} 
		
		catch (MqttException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public void unsubscribe(String topic) {
		
		subscribed.remove(topic);
		
		try {
			
			mqttClient.unsubscribe(topic);
			System.out.println("Unsubscribed");
		}
		
		catch (MqttException e) {
			
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
		messageReceivedCallback.done(topic, new String(message.getPayload()));
	}
	
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		
	}
	
	
	@Override
	public void connectionLost(Throwable arg0) {
		
		System.out.println(arg0.getMessage() + ". Reconnecting");
		mqttClient = null;
		connect();
	}

}
