package com.clearblade.java.api;

import java.util.HashMap;
import java.util.Map;

import com.clearblade.java.api.auth.Auth;
import org.eclipse.paho.client.mqttv3.*;


/**
 * MQTT client for using with the ClearBlade platform (subscription, publishing, auto-reconnect). Public final fields
 * are kept public for backwards compatibility.
 */
public class MqttClient implements MqttCallbackExtended {

	static final int QUALITY_OF_SERVICE = 0;
	static final boolean AUTO_RECONNECT = true;
	static final MqttClientPersistence MQTT_CLIENT_PERSISTENCE = null;

    @FunctionalInterface
	public interface OnConnectionComplete {
		void onConnectionComplete(boolean reconnected, String url);
	}

	@FunctionalInterface
	public interface OnConnectionLost {
	    void onConnectionLost(Throwable cause);
	}

	/**
	 * The messaging URL this client is using.
	 */
	public final String url;

	/**
	 * The authentication method to use for the connection.
	 */
	private final Auth auth;

	/**
	 * The system key of the system we are connecting to.
	 */
	public final String systemKey;

	/**
	 * Unique identifier for this MQTT client.
	 */
	public final String clientIdentifier;

	/**
	 * Quality of service to use when no specified on the publish / subscribe methods.
	 */
	private int defaultQualityOfService;

	/**
	 * Auto-reconnect when connection is lost.
	 */
	private boolean autoReconnect;

	/**
	 * paho MqttClient instance.
	 */
	protected org.eclipse.paho.client.mqttv3.MqttClient mqttClient;

	/**
	 * Contains the MessageCallback instances for each topic. This lets the user have different handling logic
	 * depending on the topic.
	 */
	protected Map<String, MessageCallback> callbackByTopic;

	/**
	 * Contains the quality of service for each topic.
	 */
	protected Map<String, Integer> qosByTopic;

	/**
	 * Functional-interface callback for connection complete events.
	 */
	private OnConnectionComplete onConnectionComplete;

	/**
	 * Functional-interface callback for connection lost events.
	 */
	private OnConnectionLost onConnectionLost;

	/**
	 * Creates a new MQTTClient instance using the given identifier. URL and auth method will be obtained from the
	 * global ClearBlade singleton.
	 */
	public MqttClient(String clientIdentifier) throws ClearBladeException {
		this(ClearBlade.getMessagingUrl(), ClearBlade.getAuth(), Util.getSystemKey(), clientIdentifier, QUALITY_OF_SERVICE, AUTO_RECONNECT);
	}

	/**
	 * Creates a new MQTTClient instance using the given identifier and quality of service. URL and auth method will
	 * be obtained from the global ClearBlade singleton.
	 */
	public MqttClient(String clientIdentifier, int qualityOfService) throws ClearBladeException {
		this(ClearBlade.getMessagingUrl(), ClearBlade.getAuth(), Util.getSystemKey(), clientIdentifier, qualityOfService, AUTO_RECONNECT);
	}

	/**
	 * Creates a new MQTTClient instance using the given information.
	 * @param url the messaging url to connect to
	 * @param auth the authentication method to use
	 * @param clientIdentifier the unique identifier for this client
	 * @param qualityOfService the default quality of service to use
	 * @param autoReconnect whenever the client should automatically connect / reconnect.
	 */
	public MqttClient(String url, Auth auth, String systemKey, String clientIdentifier, int qualityOfService, boolean autoReconnect) throws ClearBladeException {

		this.url = url;
		this.auth = auth;
		this.systemKey = systemKey;
		this.clientIdentifier = clientIdentifier;
		this.defaultQualityOfService = qualityOfService;
		this.autoReconnect = autoReconnect;
		this.mqttClient = null;
		this.callbackByTopic = new HashMap<>();
		this.qosByTopic = new HashMap<>();
		this.onConnectionComplete = null;
		this.onConnectionLost = null;

		if (autoReconnect) {
			this.connect();
		}
	}

	public int getQualityOfService() {
		return defaultQualityOfService;
	}

	public void setQualityOfService(int qualityOfService) {
		this.defaultQualityOfService = qualityOfService;
	}

	public boolean getAutoreconnect() {
	    return this.autoReconnect;
	}

	public void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	/**
	 * Callback to use when connection is complete.
	 */
	public void onConnectionComplete(OnConnectionComplete callback) {
	    this.onConnectionComplete = callback;
	}

	/**
	 * Callback to use when connection is lost.
	 */
	public void onConnectionLost(OnConnectionLost callback) {
		this.onConnectionLost = callback;
	}

	/**
	 * Tries to connect to the MQTT service specified by the supplied parameters during object construction.
	 * @throws ClearBladeException if connection fails.
	 */
	public void connect() throws ClearBladeException {
		if (mqttClient != null && mqttClient.isConnected()) {
			return;
		}
		mqttClient = connectPaho();
	}

	/**
	 * Tries to disconnect from the MQTT service. This function will always return true (due to backwards compatibility)
	 * and will instead throw an exception if disconnection fails.
	 * @return always true for backward compatibility
	 * @throws ClearBladeException if disconnection fails
	 */
	public boolean disconnect() throws ClearBladeException {

		if (mqttClient == null) {
			return true;
		}

		try {
			mqttClient.disconnect();
			mqttClient = null;

		} catch(MqttException e) {
			String errmsg = String.format("(MQTTClient) disconnect error");
			throw new ClearBladeException(errmsg, e);
		}

		return true;
	}

	/**
	 * Publishes the given message to the given topic.
	 * @param topic topic to publish to
	 * @param message message to publish
	 * @throws ClearBladeException if publish fails
	 */
	public void publish(String topic, String message) throws ClearBladeException {
		publish(topic, message.getBytes(), defaultQualityOfService);
	}

	/**
	 * Publishes the given message (bytes) to the given topic, using the given quality of service.
	 * @param topic topic to publish to
	 * @param payload message to publish
	 * @param qos quality of service
	 * @throws ClearBladeException if publish fails
	 */
	public void publish(String topic, byte[] payload, int qos) throws ClearBladeException {
		
		try {
			mqttClient.publish(topic, payload, qos, false);

		} catch (MqttException e) {
			String errmsg = String.format("(MQTTClient) publish error: %s", e.getMessage());
			throw new ClearBladeException(errmsg, e);
		}
	}

	/**
	 * Subscribes to the given topic using the given message callback for handling messages.
	 * @param topic topic to subscribe to
	 * @param callback callback to use for incoming messages or errors
     * @throws ClearBladeException when subscription fails
	 */
	public void subscribe(String topic, MessageCallback callback) throws ClearBladeException {
		subscribe(topic, defaultQualityOfService, callback);
	}

	/**
	 * Subscribes to the given topic using the given quality of service and message callback for handling messages.
	 * @param topic topic to subscribe to
	 * @param qos quality of service
	 * @param callback callback to use for incoming messages or errors
	 * @throws ClearBladeException when subscription fails
	 */
	public void subscribe(String topic, int qos, MessageCallback callback) throws ClearBladeException {
		try {
			mqttClient.subscribe(topic, qos);
			callbackByTopic.put(topic, callback);
			qosByTopic.put(topic, qos);

		} catch (MqttException e) {
			String errmsg = String.format("(MQTTClient) subscribe error: %s", e.getMessage());
			throw new ClearBladeException(errmsg, e);
		}
	}

	/**
	 * Resubscribes to all the topics this client has subscribed to so far. Any errors are reported to the message
	 * callback rather than throwing them.
	 */
	public void resubscribe() {
	    callbackByTopic.forEach((topic, callback) -> {
	    	try {
	    	    int qos = qosByTopic.get(topic);
	    		mqttClient.subscribe(topic, qos);

	    	} catch (MqttException e) {
	    		String errmsg = String.format("(MQTTClient) resubscribe error: %s", e.getMessage());
	    		callback.error(new ClearBladeException(errmsg, e));
		   }
		});
	}

	/**
	 * Unsubscribes from the given topic.
	 * @param topic topic to unsubscribe from
	 * @throws ClearBladeException when unsubscribe fails
	 */
	public boolean unsubscribe(String topic) throws ClearBladeException {
		try {
			mqttClient.unsubscribe(topic);
			callbackByTopic.remove(topic);
			qosByTopic.remove(topic);
			return true;

		} catch (MqttException e) {
		    String errmsg = String.format("(MQTTClient) unsubscribe error: %s", e.getMessage());
		    throw new ClearBladeException(errmsg, e);
		}
	}

	// MqttCallback overrides

	@Override
	public void connectComplete(boolean reconnected, String url) {

		String msg = String.format("(MQTTClient) %s complete: %s", reconnected ? "reconnection" : "connection", url);
		System.out.println(msg);

		resubscribe();

		if (this.onConnectionComplete != null) { this.onConnectionComplete.onConnectionComplete(reconnected, url); }
	}

	@Override
	public void connectionLost(Throwable arg0) {

		String msg = String.format("(MQTTClient) connection lost: %s", arg0.getMessage());
		System.out.println(msg);

		if (this.onConnectionLost != null) { this.onConnectionLost.onConnectionLost(arg0); }
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) {

		MessageCallback callback = callbackByTopic.get(topic);

		if (callback != null) {
		    callback.done(topic, message.getPayload());
		    callback.done(topic, new String(message.getPayload()));

		} else {
		    String errmsg = String.format("(MQTTClient) could not handle message for topic: %s", topic);
		    System.out.println(errmsg);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	// Misc

	/**
	 * package-protected (default when nothing is specified) method that returns a new Paho MqttClient based on
	 * the current instance.
	 */
	org.eclipse.paho.client.mqttv3.MqttClient connectPaho() throws ClearBladeException {

		if (!auth.isAuthed()) {
			throw new IllegalStateException("auth method not authenticated");
		}

		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setUserName(auth.getToken());
		options.setPassword(systemKey.toCharArray());
		options.setConnectionTimeout(5);
		options.setAutomaticReconnect(autoReconnect);

		try {
			org.eclipse.paho.client.mqttv3.MqttClient result = new org.eclipse.paho.client.mqttv3.MqttClient(url, clientIdentifier, MQTT_CLIENT_PERSISTENCE);
			result.setCallback(this);
			result.connect(options);
			return result;

		} catch (MqttException e) {
			String errmsg = String.format("(MQTTClient) could not connect to %s: %s", url, e.getMessage());
			throw new ClearBladeException(errmsg, e);
		}
	}
}

