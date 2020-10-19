package com.clearblade.java.api;

public class MessageCallback {

	/**
	 * Override to catch an error.
	 * @param exception the exception that was thrown
	 */
	public void error(ClearBladeException exception) {}

	/**
	 * Override to receive a message.
	 * @param topic topic the message is from
	 * @param message message content
	 */
	public void done(String topic, byte[] message) {}

	/**
	 * Override to receive a message as a string.
	 * @param topic topic the message is from
	 * @param message message content
	 */
	public void done(String topic, String message) {}

}
