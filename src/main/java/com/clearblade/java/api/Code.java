package com.clearblade.java.api;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Code {
	
	private String serviceName;
	private JsonObject parameters; 
	
	private RequestEngine request;

	public Code(String serName, JsonObject params){
		serviceName = serName;
		parameters = params;
		request = new RequestEngine();
	}
	
	public Code(String serName) {
		serviceName = serName;
		request = new RequestEngine();
	}
	
	public void executeWithParams(final CodeCallback callback){
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/code/" +Util.getSystemKey() + "/" + serviceName).body(parameters).build();
		request.setHeaders(headers);

		PlatformResponse result= request.execute();
		if(result.getError()) {
			Util.logger("Load", "" + result.getData(), true);
			callback.error(new ClearBladeException("Call to Save failed:"+result.getData()));
		} else {
			JsonObject codeResponse = convertJsonToJsonObject((String)result.getData());
			if(codeResponse != null){
				callback.done(codeResponse);
			}else{
				callback.error(new ClearBladeException("Failed to parse code response"));
			}
		}
	}
	
	public void executeWithoutParams(final CodeCallback callback){
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/v/1/code/" +Util.getSystemKey() + "/" + serviceName).build();
		request.setHeaders(headers);

		PlatformResponse result= request.execute();
		if(result.getError()) {
			Util.logger("Load", "" + result.getData(), true);
			callback.error(new ClearBladeException("Call to Save failed:"+result.getData()));
		} else {
			JsonObject codeResponse = convertJsonToJsonObject((String)result.getData());
			if(codeResponse != null){
				callback.done(codeResponse);
			}else{
				callback.error(new ClearBladeException("Failed to parse code response"));
			}
		}
	}
	
	private JsonObject convertJsonToJsonObject(String json) {
		// parse json string in to JsonElement
		try {
			JsonElement toObject = new JsonParser().parse(json);
			return toObject.getAsJsonObject();
		}catch(JsonSyntaxException mfe){
			return null;
		}catch(IllegalStateException ise){
			return null;
		}
	}
	
	
}
