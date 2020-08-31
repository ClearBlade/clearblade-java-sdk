package com.clearblade.java.api;




public class Util {
	
	private static String systemKey;							// system Id
	private static String systemSecret;						// system Password
	

	public static void setSystemKey(String systemKey) {
		Util.systemKey = systemKey;
	}

	public static void setSystemSecret(String systemSecret) {
		Util.systemSecret = systemSecret;
	}

	
	/**
	 * Displays internal log messages when using the API
	 * @protected
	 * @param tag The Class calling
	 * @param log The Message to display
	 * @param error is the message an Error?
	 */
	public static void logger(String tag, String log, boolean error) {
		if(ClearBlade.isLogging()) {
			if(error) {
				System.err.println(tag +": "+ log);
			} else {
				//Log.v(tag, log);
				System.out.println(tag +": "+ log);
			}
		}
	}
	
	public static String getSystemKey() {
		return systemKey;
	}

	public static String getSystemSecret() {
		return systemSecret;
	}
}
