package com.clearblade.java.api;

import java.util.HashMap;

public class MQTTClientJava {

	private static Message message;
	private static boolean isInit = false;
	private static User user;
	
	public static void main(String[] args) {
		
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
			
			message.disconnect();
			
			logoutUser();
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
		
		String systemKey = "d2f7d2dc0ab8cfbfa49cf0feb50b";
		String systemSecret = "D2F7D2DC0AD0E6AEB89AB0E6FAB501";
		String userEmail = "test114@clearblade.com";
		String userPassword = "clearblade";
		String platformURL = "https://rtp.clearblade.com";
		String messagingURL = "tcp://rtp.clearblade.com:1883";
		//user = new User(userEmail);
		HashMap<String, Object> options = new HashMap<String, Object>();
		//options.put("email", userEmail);
		//options.put("password", userPassword);
		options.put("platformURL", platformURL);
		options.put("messagingURL", messagingURL);
		
		ClearBlade.initialize(systemKey, systemSecret, options, initCallback);
	}
	
	private static void connectToMQTT() {
		
		message = new Message("clientID-test", 1);
	}
	
	private static void subscribe(String topic) {
		
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
		
		message.subscribe(topic, messageCallback);
	}
	
	private static void publish(String topic, String payload) {
		
		message.publish(topic, payload);
	}
	
	private static void logoutUser() {
		
		User currentUser = ClearBlade.getCurrentUser();
		currentUser.logout(new InitCallback() {

			@Override
			public void done(boolean results) {
				
				System.out.println("User logged out");
			}
			@Override
			public void error(ClearBladeException exception) {
				System.out.println("Logout failed " + exception.getMessage());
			}
			
		});
	}

}
