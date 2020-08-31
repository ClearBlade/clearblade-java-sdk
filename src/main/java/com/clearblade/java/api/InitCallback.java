package com.clearblade.java.api;

public abstract class InitCallback {
	public abstract void done(boolean results);
	public void error(ClearBladeException exception) {
	}
}
