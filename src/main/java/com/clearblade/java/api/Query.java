package com.clearblade.java.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.clearblade.java.api.internal.PlatformResponse;
import com.clearblade.java.api.internal.RequestEngine;
import com.clearblade.java.api.internal.RequestProperties;
import com.google.gson.*;


/**
 * This class consists exclusively of instance methods that operate on ClearBlade Collections and Items.
 * <p>
 * ClearBlade Query are objects that can query Cloud platform collections for Items.
 * A typical example would be:
 * <pre> 
 * 		Query query = new Query(collectionId);
 * 		query.equalTo("firstName", "John").greaterThan("age",40);
 *		query.fetch(new DataCallback(){
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

public class Query {
	private String collectionId;
	private boolean byName = false;
	//private String collectionName;
	private QueryObj queryObj = new QueryObj();
	private ArrayList<QueryObj> queryObjs = new ArrayList<QueryObj>();
	private int pageSize = -1;
	private int pageNum = -1;
	
	private RequestEngine request;	// used to make API requests

	/**
	 * Constructs a new Query object for modifying a collection
	 * A collection id must be set
	 */
	public Query(){
		this.request = new RequestEngine();
	}
	
	/**
	 * Constructs a new Query object for modifying a collection
	 * @param collectionId - The id of the collection to be queried
	 */
	public Query(String collectionId){
//		this.request = new RequestEngine();
//		this.setCollectionId(collectionId);
		this(collectionId, false);
	}
	
	/**
	 * Constructs a new Query object for modifying a collection
	 * @param id - The name or id of the collection to be queried
	 * @param type - a value of "collectionId" or "collectionName"
	 * 
	 */
	public Query(String id, boolean byName){
		this.request = new RequestEngine();
		this.byName = byName;
		this.setCollectionId(id, byName);	
	}
	
	/**
	 * Creates an equality clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.equalTo('name', 'John');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * //will only match if an item has an attribute 'name' that is equal to 'John'
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query equalTo(String field, Object value) {
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.EQ==null){
			queryObj.EQ = new ArrayList<FieldValue>();
		}
		queryObj.EQ.add(fv);
		return this;
	}
	
	/**
	 * Creates an non equality clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.notEqualTo('name', 'John');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * //will only match if an item has an attribute 'name' that is not equal to 'John'
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query notEqual(String field, Object value) {
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.NEQ==null){
			queryObj.NEQ = new ArrayList<FieldValue>();
		}
		queryObj.NEQ.add(fv);
		return this;
	}
	
	/**
	 * Creates an greater than clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.greaterThan('age', '18');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query greaterThan(String field, Object value) {
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.GT==null){
			queryObj.GT = new ArrayList<FieldValue>();
		}
		queryObj.GT.add(fv);
		return this;
	}
	
	/**
	 * Creates an greater than or equal to clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.greaterThanEqualTo('age', '18');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query greaterThanEqualTo(String field, Object value){
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.GTE==null){
			queryObj.GTE = new ArrayList<FieldValue>();
		}
		queryObj.GTE.add(fv);
		return this;
	}
	
	/**
	 * Creates a less than to clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.lessThan('age', '18');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query lessThan(String field, Object value) {
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.LT==null){
			queryObj.LT = new ArrayList<FieldValue>();
		}
		queryObj.LT.add(fv);
		return this;
	}
	
	/**
	 * Creates a less than to clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.lessThanEqualTo('age', '18');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for query criteria
	 * @param value - the value of the search criteria
	 * @return modified Query object for chaining purposes.
	 */
	public Query lessThanEqualTo(String field, Object value){
		FieldValue fv = new FieldValue(field, value);
		if (queryObj.LTE==null){
			queryObj.LTE = new ArrayList<FieldValue>();
		}
		queryObj.LTE.add(fv);
		return this;
	}
	
	/**
	 * Creates an ascending clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.ascending('age');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for sorting in ascending manner
	 */
	public void ascending(String field){
		
	}
	
	/**
	 * Creates an descending clause in the query object 
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.descending('age');
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for sorting in descending manner
	 */
	public void descending(String field){
		
	}
	
	public void or(Query orQuery){
		queryObjs.add(orQuery.queryObj);
		//queryObj = orQuery.queryObj;
	}
	
	/**
	 * Sets the desired page size returned by server for the query results
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.setPageSize(50);
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for sorting in descending manner
	 */
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}
	
	/**
	 * Sets the page number of the query results to be returned by the server
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.setPageNum(2);
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 * });
	 * </pre>
	 * @param field - name of the column to be used for sorting in descending manner
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
	/**
	 * Executes the query built by the Query Class. Will return a QueryResponse object
	 * which contains an array of the Items, and pagination information
	 * <pre>
	 * Query query = new Query(collectionId);
	 * query.offset(75);
	 * query.fetch(new DataCallback{
	 * 	  public void done(QueryResponse resp){
	 *       //your logic here
	 *    }
	 *	  public void error(ClearBladeException exception){
	 *		 //error handling
	 *    }
	 * });
	 * </pre>
	 * @param callback - a DataCallback to be called upon success/failure of the query. 
	 */
	public void fetch(final DataCallback callback) {
//		DataTask asyncFetch = new DataTask(new PlatformCallback(this, callback) {
//			@Override
//			public void done(String response) {
//				QueryResponse resp = new QueryResponse();
//				resp.setDataItems(parseItemArray(response));
//				callback.done(resp);
//			}
//
//			@Override
//			public void error(ClearBladeException exception) {
//				callback.error(exception);
//			}
//
//		});
//		asyncFetch.execute(request);

        try {
            QueryResponse resp = doFetch();
            callback.done(resp);
            callback.done(resp.getDataItems());

		} catch (ClearBladeException e) {
        	callback.error(e);
		}
	}
	
	public Item[] fetchSync() throws ClearBladeException{
		QueryResponse resp = doFetch();
		return resp.getDataItems();
	}
	
	protected QueryResponse doFetch() throws ClearBladeException {

		fetchSetup();

		PlatformResponse<String> result = request.execute();

		if(result.isError()) {
			Util.logger("Load", result.getData(), true);
			String errmsg = String.format("Call to fetch failed: %s", result.getData());
			throw new ClearBladeException(errmsg);

		} else {
			QueryResponse resp = QueryResponse.parseJson(result.getData());
			resp.setDataItems(parseItemArray(resp.getData().toString()));
			return resp;
		}
	}

	protected void fetchSetup(){
		String queryParam = getFetchURLParameter();
		RequestProperties headers;
		headers = new RequestProperties.Builder().method("GET").endPoint(getEndPoint()+ queryParam).build();
		//System.out.println(headers.getUri());
		request.setHeaders(headers);
	}

//	public Item[] fetch(){
//		return null;
//	}
	
	protected String filtersAsJsonString() {
		ArrayList<QueryObj> temp = queryObjs;
		if (queryObjs.size()==0) {
			//we havent done an or, so just build up an array of the queryObj
			//we can use the queryObjs list becuase the user may continue to build on the Query
			//for future use
			temp = new ArrayList<QueryObj>();
		}
		temp.add(queryObj);
		String param = "";//gson.toJson(temp);
		Iterator<QueryObj> it = temp.iterator();
		while(it.hasNext())
		{
		    QueryObj obj = it.next();
		    param = param+"["+stringifyQuery(obj);
		    if (it.hasNext()){
		    	//there is an or
		    	param=param+"],";
		    }
		}
		if (param.length()>1) {
			param="["+param+"]]";
		}else{
			return null;
		}
		
		return param;
	}
	
	/**
	 * Internal only, made public for test and verification.  Returns the query string parameter necessary to implement the fetch query
	 * @return String
	 */
	public String getFetchURLParameter(){
		String param = "{";
		//add filters to url param
		if(filtersAsJsonString() != null){
			param += "\"FILTERS\":" + filtersAsJsonString();
		}else{
			//no queries specified, so set pagenum as 0 to get all data
			param += "\"PAGENUM\":" + 0; 
		}
		//if defined add page num
		if(this.pageNum >= 0){
			param += ",\"PAGENUM\":" + this.pageNum;
		}
		//if defined add page size
		if(this.pageSize >= 0){
			param += ",\"PAGESIZE\":" + this.pageSize;
		}
		//TODO: if defined add sort
		param += "}";
		try {
			param = URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (param.length()>0){
			param = "?query="+param;
		}
		return param;
	}
	
	protected String queryAsJsonString() {
		ArrayList<QueryObj> temp = queryObjs;
		if (queryObjs.size()==0) {
			//we havent done an or, so just build up an array of the queryObj
			//we can use the queryObjs list becuase the user may continue to build on the Query
			//for future use
			temp = new ArrayList<QueryObj>();
			temp.add(queryObj);
		}
		String param = "";//gson.toJson(temp);
		Iterator<QueryObj> it = temp.iterator();
		while(it.hasNext())
		{
		    QueryObj obj = it.next();
		    param = param+stringifyQuery(obj);
		    if (it.hasNext()){
		    	//there is an or
		    	param=param+",";
		    }
		}
		if (param.length()>0) {
			param="["+param+"]";
			//add extra brackets for bug in platform
			param = "["+param+"]";
		}
		return param;
	}
	
	/**
	 * Internal only, made public for test and verification.  Returns the query string parameter necessary to implement the query
	 * @return String
	 */
	public String getURLParameter(){
		String param = queryAsJsonString();
		try {
			param = URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (param.length()>0){
			param = "?query="+param;
		}
		return param;
	}
	
	private String stringifyQuery(QueryObj obj){
		String ret = "";
		ret= ret + stringifyParam("EQ", obj.EQ);
		ret= ret + stringifyParam("GT", obj.GT);
		ret= ret + stringifyParam("GTE", obj.GTE);
		ret= ret + stringifyParam("LT", obj.LT);
		ret= ret + stringifyParam("LTE", obj.LTE);
		ret= ret + stringifyParam("NEQ", obj.NEQ);
		if (ret.length()>0) {
			ret = "{"+ ret+ "}";
		}
		return ret;
	}
	
	private String stringifyParam(String paramType, ArrayList<FieldValue> params){
		if (params == null || params.size()==0){return "";}
		
		String ret="\""+paramType+"\":[";
		Iterator<FieldValue> iter = params.iterator();
		while(iter.hasNext()){
			FieldValue fv = (FieldValue) iter.next();
			Class valueClass = fv.value.getClass();
			if(valueClass.getName().toLowerCase().equalsIgnoreCase("java.lang.string")){
				ret = ret + "{\""+fv.field+"\":\""+fv.value+"\"}";
			}else if(valueClass.getName().toLowerCase().equalsIgnoreCase("java.lang.integer")){
				ret = ret + "{\""+fv.field+"\":"+Integer.toString((Integer) fv.value)+"}";
			}
			if (iter.hasNext()){
				ret=ret+",";
			}
		}
		ret=ret+"]";
		return ret;
	}
	
	private HashMap<String,Object> changes = new HashMap<String,Object>();
	
	/**
	 * Adds a change set to the query.  When update is run all of the changes
	 * are honored across the matching criteria 
	 * @param name - the name of the column to be modified
	 * @param value - the new value to insert into the column
	 */
	public void addChange(String name, Object value){
		changes.put(name, value);
	}
	
	/**
	 * Clears the changes currently set in they query.  Update will perform no action after this 
	 * is executed.
	 */
	public void clearChanges(){
		changes = new HashMap<String, Object>();
	}
	
	/**
	 * Call an update on all items matching the query criteria to conform to the changes that have been added via the addChange method
	 * <pre>
	 * Query query = new Query(collectionText.getText().toString());
	 *	query.equalTo("name", "John");
	 *	query.addChange("name", "Johan");
	 *	query.update( new DataCallback() {
	 *
	 *		@Override
	 *		public void done(Item[] response) {
	 *		}
	 *
	 *		@Override
	 *		public void error(ClearBladeException exception) {
	 *			
	 *		}
	 *	});
	 * </pre>
	 * @param callback
	 */
	public void update(final DataCallback callback) {
		updateSetup();
//		DataTask asyncFetch = new DataTask(new PlatformCallback(this, callback){
//
//			@Override
//			public void done(String response) {
//				Item[] ret = parseItemArray(response);
//				callback.done(ret);
//			}
//
//			@Override
//			public void error(ClearBladeException exception) {
//				callback.error(exception);
//			}
//			
//		});
//		asyncFetch.execute(request);
		changes = new HashMap<String,Object>();
		
		PlatformResponse result= request.execute();
		if(result.isError()) {
			Util.logger("Load", "" + result.getData(), true);
			callback.error(new ClearBladeException("Call to fetch failed:"+result.getData()));
		} else {
			Item[] ret = parseItemArray((String) result.getData());
			callback.done(ret);
		}
		
		
	}
	
	public Item[] updateSync() throws ClearBladeException{
		updateSetup();
		PlatformResponse resp = request.execute();
		Item[] ret;
		if(resp.isError()) {
			throw new ClearBladeException("Call to fetch failed:"+resp.getData());
		} else {
			ret = parseItemArray((String) resp.getData());
		}
		return ret;
	}
	
	private void updateSetup(){
		JsonObject payload = new JsonObject();
		payload.addProperty("$set", changeSetMapAsJsonString());
		//JsonObject query = new JsonObject();
		JsonElement toObject = new JsonParser().parse(queryAsJsonString());
		payload.add("query", toObject);
		String endPoint = "";
		
		RequestProperties headers = new RequestProperties.Builder().method("PUT").endPoint(getEndPoint()).body(payload).build();
		request.setHeaders(headers);
	}
	
	private String changeSetMapAsJsonString(){
		
		String jsonString = "{";
		
		for (Map.Entry<String, Object> entry : changes.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    if(value.getClass().getName().equalsIgnoreCase("java.lang.string")){
		    	jsonString += "\"" + key + "\":" + "\"" + value + "\"";
		    }else if(value.getClass().getName().equalsIgnoreCase("java.lang.integer")){
		    	jsonString += "\"" + key + "\":" + value.toString();
		    }
		    jsonString += ",";
		}
		
		//remove trailing comma
		jsonString = jsonString.substring(0, jsonString.length()-1);
		
		jsonString += "}";
		
		return jsonString;
		
	}
	
	/**
	 * Removes on all items matching the query criteria within a Collection
	 * <pre>Query query = new Query(collectionText.getText().toString());
	 *	query.equalTo("name", "John");
	 *	query.remove( new DataCallback() {
	 *
	 *		@Override
	 *		public void done(Item[] response) {
	 *		}
	 *
	 *		@Override
	 *		public void error(ClearBladeException exception) {
	 *			
	 *		}
	 *	});
	 * </pre>
	 * @param callback
	 */
	public void remove(final DataCallback callback)  {

		String queryParam = getURLParameter();
		String endPoint = "";
		
		RequestProperties headers = new RequestProperties.Builder().method("DELETE").endPoint(getEndPoint()+ queryParam).build();
		request.setHeaders(headers);
		
//		DataTask asyncFetch = new DataTask(new PlatformCallback(this, callback){
//
//			@Override
//			public void done(String response) {
//				Item[] ret = parseItemArray(response);
//				callback.done(ret);
//			}
//
//			@Override
//			public void error(ClearBladeException exception) {
//				callback.error(exception);
//			}
//			
//		});
//		asyncFetch.execute(request);
		
		PlatformResponse result= request.execute();
		if(result.isError()) {
			Util.logger("Load", "" + result.getData(), true);
			callback.error(new ClearBladeException("Call to remove failed:"+result.getData()));
		} else {
			Item[] ret = parseItemArray((String) result.getData());
			callback.done(ret);
			
		}
	}
	
	private String getEndPoint(){
		String endPoint = "";
		if(byName){
			endPoint = "api/v/1/collection/"+Util.getSystemKey() +"/" +collectionId;
		}else{
			endPoint = "api/v/1/data/" +collectionId;
		}
		return endPoint;
	}
	public Item[] removeSync() throws ClearBladeException{
		removeSetup();
		PlatformResponse resp = request.execute();
		Item[] ret;
		if(resp.isError()) {
			throw new ClearBladeException("Call to fetch failed:"+resp.getData());
		} else {
			ret = parseItemArray((String) resp.getData());
		}
		return ret;
	}
	
	private void removeSetup(){
		String queryParam = getURLParameter();
		
		RequestProperties headers = new RequestProperties.Builder().method("DELETE").endPoint(getEndPoint()+ queryParam).build();
		request.setHeaders(headers);
	}

	/**
	 * @return the collectionId
	 */
	public String getCollectionId() {
		return collectionId;
	}

	/**
	 * @param collectionId the collectionId to set
	 */
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	
	/**
	 * @param collectionId the collectionId to set
	 */
	public void setCollectionId(String collectionId, boolean byName) {
		this.collectionId = collectionId;
		this.byName = byName;
	}
	
	private class FieldValue{
		public String field;
		public Object value;
		
		public FieldValue(String field, Object value){
			this.field = field;
			this.value = value;
		}
	}
	private class QueryObj{
		public ArrayList<FieldValue> EQ;
		public ArrayList<FieldValue> GT;
		public ArrayList<FieldValue> GTE;
		public ArrayList<FieldValue> LT;
		public ArrayList<FieldValue> LTE;
		public ArrayList<FieldValue> NEQ;
		
	}

	/**
	 * Similar to {@link #parseItemArrayWith(String, String, boolean)} but gets the collection ID and by name
     * properties from the current instance.
	 * @param rawJson raw Json string to parse
	 * @return Item array
	 */
	private Item[] parseItemArray(String rawJson) {
		return parseItemArrayWith(rawJson, getCollectionId(), byName);
	}

	/**
	 * Parses the given string as an item array. Entries that are not objects or empty objects will be ignored.
	 * @param rawJson raw Json string to parse
	 * @return Item array
	 */
	protected static Item[] parseItemArrayWith(String rawJson, String collectionId, boolean byName) {
		Gson gson = new Gson();

		JsonElement[] arr = gson.fromJson(rawJson, JsonElement[].class);

		ArrayList<Item> result = new ArrayList<>();

		for (JsonElement elem : arr) {

			if (!elem.isJsonObject()) {
				continue;
			}

			JsonObject obj = elem.getAsJsonObject();

			if (obj.size() <= 0) {
			    continue;
			}

			result.add(new Item(obj, collectionId, byName));
		}

		Item[] ret = new Item[result.size()];
		return result.toArray(ret);
	}

}
