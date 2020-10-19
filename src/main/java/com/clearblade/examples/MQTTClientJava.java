package com.clearblade.examples;

import com.clearblade.java.api.*;

import java.util.HashMap;

public class MQTTClientJava {

	private static MQTTClient mqttClient;
	private static boolean isInit = false;

	public static void main(String[] args) throws ClearBladeException{
		
		initClearBlade();
		if (isInit) {
			
			connectToMQTT();
			
			try {
				Thread.sleep(2000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			subscribe("hello");
			
			try {
				Thread.sleep(5000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			publish("hello", "this is a test");
			
			try {
				Thread.sleep(5000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			mqttClient.disconnect();
			
			logout();
		}
	}
	
	private static void initClearBlade() {
		
		InitCallback initCallback = new InitCallback() {
			
			@Override
			public void done(boolean results) {
				
				System.out.println("ClearBlade platform initialized");
				isInit = true;
			}
			
			@Override
			public void error(ClearBladeException error) {
				
				isInit = false;
				String message = error.getMessage();
				System.out.println(message);
			}
		};
		
		String systemKey = "[SYSTEM KEY GOES HERE]";
		String systemSecret = "[SYSTEM SECRET GOES HERE]";
		String userEmail = "[USER EMAIL GOES HERE]";
		String userPassword = "[USER PASSWORD GOES HERE]";
		String platformUrl = "[PLATFORM URL GOES HERE]";
		String messagingUrl = "[MESSAGING URL GOES HERE]";

		HashMap<String, Object> options = new HashMap<String, Object>();

		options.put("email", userEmail);
		options.put("password", userPassword);
		options.put("platformURL", platformUrl);
		options.put("messagingURL", messagingUrl);
		
		ClearBlade.initialize(systemKey, systemSecret, options, initCallback);
	}
	
	private static void connectToMQTT() throws ClearBladeException {
		mqttClient = new MQTTClient("clientID-test", 1);
	}
	
	private static void subscribe(String topic) throws ClearBladeException {
		
		MessageCallback messageCallback = new MessageCallback() {
			
			@Override
			public void done(String topic, String message){
				
				System.out.println("Topic: " + topic +" Message received: " + message);
			}
			
			@Override
			public void error(ClearBladeException exception) {
				
				String message = exception.getLocalizedMessage();
				System.out.println("CB Subscribe Exception: " + message);
			}
		};
		
		mqttClient.subscribe(topic, messageCallback);
	}
	
	private static void publish(String topic, String payload) throws ClearBladeException {
		mqttClient.publish(topic, payload);
	}
	
	private static void logout() {
		try {
			ClearBlade.getAuth().doLogout();
			System.out.println("logged out");
		} catch (Exception e) {
			System.out.println("logout failed: " + e.getMessage());
		}
	}

}
