package com.clearblade.java.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;

public class QueryResponse {

	private int CURRENTPAGE;
	private String NEXTPAGEURL;
	private String PREVPAGEURL;

	private int TOTAL;
	private JsonArray DATA;

	@Expose(serialize = false, deserialize = false)
	private Item[] dataItems;
	
	public QueryResponse() {
		this.dataItems = new Item[0];
	}

	public static QueryResponse parseJson(String rawJson) {
		Gson gson = new Gson();
		return gson.fromJson(rawJson, QueryResponse.class);
	}

	public int getCurrentPage() {
		return CURRENTPAGE;
	}

	public String getNextPageURL() {
		return NEXTPAGEURL;
	}

	public String getPrevPageURL() {
		return PREVPAGEURL;
	}

	public int getTotalCount() {
		return TOTAL;
	}

	public JsonArray getData() {
		return DATA;
	}

	public Item[] getDataItems() {
		return dataItems;
	}

	public void setDataItems(Item[] items) {
		dataItems = items;
	}

}
