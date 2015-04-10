package com.clearblade.java.api;

import com.google.gson.JsonObject;

public abstract class CodeCallback {
	public void done(JsonObject response){
		
	}
	public void error(ClearBladeException exception){
		
	}
}


