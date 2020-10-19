package com.clearblade.java.api;

import com.clearblade.java.api.auth.Auth;
import com.clearblade.java.api.auth.AuthException;

import java.util.HashMap;
import java.util.Map;


/**
 * This class consists exclusively of static methods that manage API configurations, initialization, and API information
 * <p>This class consists of static methods that:
 * <ul>
 *  <li>Manage configurations for the ClearBlade API</li>
 *  <li>Provide SDK and API information</li>
 *  <li>Initialize the ClearBlade Library for use</li>
 * </ul>
 * <strong>*You must call initialize(String systemKey, String systemSecret) or its other variants to initialize the API*</strong>
 * </p>
 *
 * @author  Clyde Byrd III, Aaron Allsbrook, Michael Sprague
 * @since   1.0
 */
public class ClearBlade {
	private static final String TAG = "ClearBlade"; 		
	private final static String apiVersion = "0.0.1";
	private final static String sdkVersion = "0.0.1";

	private static User user;								// Current User of the application. Not implemented Yet
	private static boolean initError = false;
	private static String masterSecret;					    // App's Admin Password; has access to Everything

	private static InitOptions _initOptions;

	/**
	 * Returns the version of the API that is currently in use.
	 * 
	 * @return The API version
	 */
	public static String getApiVersion() {
		return apiVersion;
	}

	/**
	 * Returns the version of the SDK  in use
	 * @return SDK version
	 */
	public static String getSdkVersion() {
		return sdkVersion;
	}

	/**
	 * getInitOptions returns the current InitOptions instance.
	 * @return InitOptions instance.
	 */
	public static InitOptions getInitOptions() {
	    return _initOptions;
	}

	/**
	 * Returns the current Auth method
	 * @return Current auth method
	 */
	public static Auth getAuth() {
		return _initOptions.getAuth();
	}

	// --------------------------------
	// Getters and setters around InitOptions for backwards compatibility
	// --------------------------------

	/**
	 * Returns the uri of the backend that will be used for API calls
	 * @return uri of the backend
	 */
	public static String getPlatformUrl() {
		return _initOptions.getPlatformUrl();
	}

	/**
	 * @deprecated use {@link #getPlatformUrl()} instead.
	 */
	@Deprecated
	public static String getUri() {
		return getPlatformUrl();
	}

	/**
	 * Sets URL of the backend platform that will be used for API calls
	 * Typically scenarios are https://platform.clearblade.com
	 */
	public static void setPlatformUrl(String platformUrl){
		_initOptions.setPlatformUrl(platformUrl);
	}

	/**
	 * @deprecated use {@link #setPlatformUrl(String)} instead.
	 */
	@Deprecated
	public static void setUri(String platformUrl) {
		setPlatformUrl(platformUrl);
	}

	/**
	 * Sets the url of the message broker that will be used in messaging applications
	 * Defaults to 'tcp://platform.clearblade.com:1883'
	 * @param messagingUrl the string that will be set as the url
	 */
	public static void setMessagingUrl(String messagingUrl) {
		_initOptions.setMessagingUrl(messagingUrl);
	}

	/**
	 * @deprecated use {@link #setMessagingUrl(String)} instead.
	 */
	@Deprecated
	public static void setMessageUrl(String messagingUrl) {
		setMessagingUrl(messagingUrl);
	}

	/**
	 * Gets the url of the message broker that was set upon initialization
	 * @return URL of message broker
	 */
	public static String getMessagingUrl() {
		return _initOptions.getMessagingUrl();
	}

	/**
	 * @deprecated use {@link #getMessagingUrl()} instead.
	 */
	@Deprecated
	public static String getMessageUrl() {
		return getMessagingUrl();
	}

	/**
	 * Returns a boolean that specifies if the API will show internal Logs
	 * @return logging boolean
	 */
	public static boolean isLogging() {
	    return _initOptions.isEnableLogging();
	}

	/**
	 * If value is true, internal API logs will be displayed throughout the use of the
	 * API else no internal logs displayed
	 * @param value determines API logging
	 */
	public static void setLogging(boolean value) {
	    _initOptions.setEnableLogging(value);
	}

	/**
	 * Returns the milliseconds that the API will wait for a connection to the backend
	 * until API requests are aborted.
	 * @return milliseconds
	 */
	public static int getCallTimeout() {
		return _initOptions.getCallTimeout();
	}

	/**
	 * Sets the time in milliseconds that an http Request will wait for a
	 * connection with the backend until it is aborted.
	 * @param timeout milliseconds until http request is aborted
	 */
	public static void setCallTimeout(int timeout) {
		_initOptions.setCallTimeout(timeout);
	}

	/**
	 * Allows for passing requests to an untrusted server.  This method
	 * is not recommended for any scenario other than development
	 */
	public static void setAllowUntrusted(boolean allowUntrustedCertificates){
		_initOptions.setAllowUntrusted(allowUntrustedCertificates);
	}

	/**
	 * Allows for passing requests to an untrusted server.
	 * @return boolean value for using untrusted backend servers
	 */
	public static boolean isAllowUntrusted(){
		return _initOptions.isAllowUntrusted();
	}

	/**
	 * @deprecated use {@link #isAllowUntrusted()} instead.
	 */
	@Deprecated
	public static boolean getAllowUntrusted(){
		return _initOptions.isAllowUntrusted();
	}

	// --------------------------------
	// Initialize methods
	// --------------------------------

	/**
	 * Initializes the API using the given {@link com.clearblade.java.api.InitOptions}.
	 * @param systemKey the system key to use.
	 * @param systemSecret the system secret to use.
	 * @param options the options to use when initializing the SDK.
	 * @param callback the callback object to use on success/error.
	 */
    public static void initialize(String systemKey, String systemSecret, InitOptions options, InitCallback callback) {

    	// uses copy constructor on given options

    	_initOptions = new InitOptions(options);

	    // checks system parameters

		if (systemKey == null || systemKey.length() <= 0) {
		    throw new IllegalArgumentException("systemKey must be a non-empty String");
		}

		if (systemSecret == null || systemSecret.length() <= 0) {
			throw new IllegalArgumentException("systemSecret must be a non-empty String");
		}

		// sets global system info

		Util.setSystemKey(systemKey);
		Util.setSystemSecret(systemSecret);

		// authenticates

		try {
			Auth auth = options.getAuth();
			auth.doAuth();
			callback.done(true);

		} catch (AuthException e) {
			String errmsg = String.format("authentication error: %s", e.getMessage());
			callback.error(new ClearBladeException(errmsg, e));
		}
	}

	/**
	 * Similar to {@link #initialize(String, String, InitOptions, InitCallback)}
	 * but uses the default InitOptions.
	 */
	public static void initialize(String systemKey, String systemSecret, InitCallback callback) {
	    initialize(systemKey, systemSecret, new InitOptions(), callback);
	}

	/**
	 * Initializes the API the given legacy init options map. Available init options are:
	 *
	 * 	 email - String to register or log-in as specific user (required if password is given)
	 * 	 password - password String for given user (required if email is given)
	 * 	 platformURL - Custom Platform URL
	 * 	 messagingURL - Custom Messaging URL
	 * 	 registerUser - Boolean to tell if you'd like to attempt registering the given
	 * 	 logging - Boolean to enable ClearBlade Internal API logging
	 * 	 callTimeout - Int number of milliseconds for call timeouts
	 *   allowUntrusted - Boolean to connect to a platform server without a signed SSL certificate
     *
	 * @param systemKey The key used to identify the System in use
	 * @param systemSecret The secret used to verify the System in use
	 * @param optionsMap Map of initialization options
	 * @param callback InitCallback for when initialization is done (success of failure)
     *
	 * @throws IllegalArgumentException if system key or system secret are missing
     *
	 * @deprecated use {@link #initialize(String, String, InitOptions, InitCallback)} instead.
	 */
	@Deprecated
	public static void initialize(String systemKey, String systemSecret, Map<String,Object> optionsMap, InitCallback callback){
	    InitOptions initOptions = initOptionsFromMap(optionsMap);
	    initialize(systemKey, systemSecret, initOptions, callback);
	}

	// --------------------------------
	// Utility
	// --------------------------------

	/**
	 * initOptionsFromMap converts a legacy initOptions Map object into an
	 * InitOptions instance.
	 * @param options the options to convert.
	 * @return InitOptions instance.
	 */
	private static InitOptions initOptionsFromMap(Map<String, Object> options) {

	    InitOptions result = new InitOptions();

		String platformUrl = (String) options.get("platformURL");
		if (platformUrl != null) {
			result.setPlatformUrl(platformUrl);
		}

		String messagingUrl = (String) options.get("messagingURL");
		if (messagingUrl != null) {
			result.setMessagingUrl(messagingUrl);
		}

		String email = (String) options.get("email");
		if (email != null) {
			result.setEmail(email);
		}

		String password = (String) options.get("password");
		if (password != null) {
			result.setPassword(password);
		}

		Boolean logging = (Boolean) options.get("logging");
		if (logging != null) {
			result.setEnableLogging(logging);
		}

		Integer timeout = (Integer) options.get("callTimeout");
		if (timeout != null) {
			result.setCallTimeout(timeout);
		}

//		//init registerUser
//		Boolean registerUser = (Boolean) initOptions.get("registerUser");
//		if(registerUser == null){
//			registerUser = false;
//		}
//
		Boolean allowUntrusted = (Boolean) options.get("allowUntrusted");
		if(allowUntrusted != null){
			result.setAllowUntrusted(allowUntrusted);
		}

	    return result;
	}

	// --------------------------------
    // Misc
	// --------------------------------

	/**
	 * <p>Sets the masterSecret of the Application.
	 * If masterSecret is set it will be used instead
	 * of the appSecret. It gives complete access to APP resources
	 * </p>
	 * <strong>Never use the masterSecret in production code.</strong>
	 * @param myMasterSecret - The Applications Admin secret
	 */
	public static void setMasterSecret(String myMasterSecret) {
		masterSecret = myMasterSecret;
	}

	public static void setInitError(boolean value) {
		initError = value;
	}
}

