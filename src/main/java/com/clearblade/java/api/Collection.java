package com.clearblade.java.api;

import java.util.ArrayList;
import java.util.Iterator;

import com.clearblade.java.api.ClearBladeException;
import com.clearblade.java.api.DataCallback;
import com.clearblade.java.api.Query;
import com.clearblade.java.api.QueryResponse;
import com.clearblade.java.api.internal.PlatformCallback;
import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * This class consists exclusively of instance methods that operate on ClearBlade Collections.
 * <p>
 * ClearBlade Collections are objects that can query Cloud backend collections for Items.
 * A typical example would be:
 * <pre> 
 * 		Collection collection = new Collection(collectionId);
 *		collection.fetchAll(new DataCallback(){
 *
 *			@Override
 *			public void done(QueryResponse resp) {
 *				String msg = "";
 *				for (int i=0;i<resp.getDataItems().length;i++){
 *					msg = msg + resp.getDataItems()[i].toString()+",";
 *				}	
 *			}
 *
 *			@Override
 *			public void error(ClearBladeException exception) {
 *				// TODO Auto-generated method stub				
 *			}
 *		});
 * </pre>
 * </p>
 *
 * @author  Clyde Byrd, Aaron Allsbrook
 * @see Item
 * @see Query
 * @see ClearBladeException
 * @since   1.0
 * 
 */
public class Collection implements Iterable<Item>{

	private class ArrayIterator implements Iterator<Item> {
		// since itemArray.length is N, we decrement it immediately to N - 1 in next()
		private int i = itemArray.length; 
	
		public boolean hasNext() { return i > 0;}
		public Item next() { return itemArray[--i];} 
		public void remove() {
			throw new UnsupportedOperationException("remove is not supported.");
		}
	}
	private final String TAG = "CLEARBLADECOLLECTION";
	private String collectionId;			// Type of collection
	
	private Query query;			// string to filter data by
	private Item[] itemArray;		// array that stores all Items
	
	private boolean byName = false;
	private RequestEngine request;	// used to make API requests

	/**
	 * Constructs a new ClearBladeCollection of the specified type
	 * 
	 * @param id the Id of the Collection
	 */
	public Collection(String id) {
		this.collectionId = id;
		this.query = null;
		//this.request = new RequestEngine();
		this.itemArray = null;
	}
	
	public Collection( String id, boolean byName) {
		this.query = null;
		//this.request = new RequestEngine();
		this.itemArray = null;
		this.collectionId = id;
		this.byName = byName;
	}
	
	public void create(String columns, final DataCallback callback) {
		JsonObject cols = convertJsonToJsonObject(columns);
		request = new RequestEngine();
		RequestProperties headers = new RequestProperties.Builder().method("POST").endPoint("api/data/" + collectionId).body(cols).build();
		request.setHeaders(headers);
		PlatformResponse result= request.execute();
		if(result.getError()) {
			Util.logger("Load", "" + result.getData(), true);
			callback.error(new ClearBladeException("Call to fetch failed:"+result.getData()));
		} else {
			QueryResponse resp = new QueryResponse();
			resp.setDataItems(convertJsonArrayToItemArray((String)result.getData()));
			callback.done(resp);
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
	
	public void update(final DataCallback callback) {
		Query query = new Query();
		query.setCollectionId(collectionId);
		query.fetch(new DataCallback(){

			@Override
			public void done(QueryResponse response) {
				itemArray = response.getDataItems();
				callback.done(response);
			}

			@Override
			public void error(ClearBladeException exception) {
				callback.error(exception);
			}
			
		});
		
	}
	
	/** 
	 * Deletes all Items that are saved in the collection in the Cloud synchronously.
	 * <p>Deleted Items will be stored locally in the Collection.</p>
	 * <strong>*Overrides previously stored Items*</strong>
	 * <strong>*Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in the callback error function
	 */
	public void remove(DataCallback callback) {
		Query query = new Query(collectionId, byName);
		query.remove(callback);
	}

	/**
	 * Returns all items in the collection as item[].
	 * @private
	 * @param json A JSON Array in string format
	 * @return Item[] An array of Items
	 * @throws ClearBladeException will be thrown if Collection was Empty!
	 */
	public Item[] getItems(){
		return itemArray;
	}
//	private Item[] convertJsonArrayToItemArray(String json) {
//		// Parse the JSON string in to a JsonElement
//		JsonElement jsonArrayString = new JsonParser().parse(json);
//		// Store the JsonElement as a JsonArray
//		JsonArray array = jsonArrayString.getAsJsonArray();
//		// If array size is 0, the Collection was empty; Throw Error
//		if(array.size() == 0) {
//		//	throw new ClearBladeException("Collection was Empty!");
//		}
//		
//		// Create Item Array and initialize its values
//		Item[] items = new Item[array.size()];
//		
//		for(int i = 0, len = array.size(); i < len; i++){
//			items[i] = new Item(array.get(i).getAsJsonObject().toString(), this.id);
//		}
//		
//		return items;
//	}

	
	/** 
	 * Gets all Items that match Query criteria from the platform in the Cloud.
	 * <p>Retrieved Items will be stored locally in the Collection.</p>
	 * <strong>*Overrides previously stored Items*</strong>
	 * <strong>*Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in callback.error() if the collection was empty
	 */
	public void fetch(Query query, final DataCallback callback) {
	
		query.setCollectionId(collectionId, byName);
		query.fetch(new DataCallback(){

			@Override
			public void done(QueryResponse response) {
				itemArray = response.getDataItems();
				callback.done(response);
			}

			@Override
			public void error(ClearBladeException exception) {
				callback.error(exception);
			}
			
		});
	}
	
	/** 
	 * Gets all Items that match Query criteria from the platform in the Cloud.
	 * <p>Retrieved Items will be stored locally in the Collection.</p>
	 * <strong>*Overrides previously stored Items*</strong>
	 * <strong>*Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in callback.error() if the collection was empty
	 */
	public void fetch(final DataCallback callback) {
		fetch(query, callback);
	}
	
	/** 
	 * Gets all Items that are saved in the collection in the Cloud.
	 * <p>Retrieved Items will be stored locally in the Collection.</p>
	 * <strong>*Overrides previously stored Items*</strong>
	 * <strong>*Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in callback.error() if the collection was empty
	 */
	public void fetchAll(final DataCallback callback) {
		Query query = new Query(collectionId, byName);
		query.fetch(new DataCallback(){

			@Override
			public void done(QueryResponse response) {
				itemArray = response.getDataItems();
				callback.done(response);
			}

			@Override
			public void error(ClearBladeException exception) {
				callback.error(exception);
			}
			
		});
	}
	
	public Item[] fetchAllSync() throws ClearBladeException{
		Query query = new Query(collectionId, byName);
	
		return query.fetchSync();
	}

	/**
	 * Returns the query to be performed during a call to ClearBladeCollection.fetch(). 
	 * will be null if not set by setQuery().
	 * @return query The conditional query to perform on a Cloud Collection
	 */
	public Query getQuery() {
		return this.query;
	}

	/**
	 * Returns an Item Iterator for the ClearBladeCollection.
	 * <p>Will point at the first Item in the collection that is retrieved from a ClearBladeCollection.fetch() or ClearBladeCollection.clear(). </p>
	 * @throws RuntimeException will be thrown if a call to ClearBladeCollection.fetch() or ClearBladeCollection.clear() has not been made 
	 */
	@Override
	public Iterator<Item> iterator() {
		// If itemArray is null; Throw a ClearBladeException
		if(itemArray == null){
			throw new RuntimeException("You can not Iterate over an Empty Collection");
		}
		return new ArrayIterator();

	}

	/**
	 * Sets the query to be performed during a call to ClearBladeCollection.get()
	 * @param query A conditional String that will determine what Items to retrieve from the Cloud Database
	 */
	public void setQuery(Query query) {
		this.query = query;
	}
	

	/**
	 * Helper method for debugging collection contents
	 */
	public String toString() {
		String ret = "";
		Iterator<Item> iter = iterator();
		
		while (iter.hasNext()){
			Item temp = iter.next();
			ret = ret + temp.toString();
		}
		return ret;
	}
	
	/**
	 * @return the collectionId
	 */
	public String getCollectionId() {
		return collectionId;
	}
	
	private Item[] convertJsonArrayToItemArray(String json) {
		// Parse the JSON string in to a JsonElement
		JsonElement jsonElement = new JsonParser().parse(json);
		// Store the JsonElement as a JsonArray
		JsonArray array = jsonElement.getAsJsonArray();
		ArrayList<Item> items = new ArrayList<Item>();// new Item[array.size()];
		Iterator<JsonElement> iter = array.iterator();
		while(iter.hasNext()){
			
			JsonElement val = iter.next();
			if (val.isJsonObject()){
				JsonObject temp = val.getAsJsonObject();
				if (temp.entrySet().size()==0){
					return (new Item[0]);
				}else {
					items.add(new Item(temp, getCollectionId(), byName));
//					for (Entry<String, JsonElement> entry : temp.entrySet()) {
//					    JsonObject elementTemp = entry.getValue().getAsJsonObject();//.getAsJsonArray("unterfeld");
//					    
//					    items.add(new Item(entry, getCollectionId()));
//					    System.out.println("lets take a peak at the member");
//					}
				}
			} 
		}
		
		Item[] ret = new Item[items.size()];
		ret = (Item[]) items.toArray(ret);
		return ret;
	}
	
}
