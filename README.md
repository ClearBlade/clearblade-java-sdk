# Quickstart

The ClearBlade Java SDK uses [Maven](https://maven.apache.org/) to manage the project and its dependencies. Since
most IDEs support Maven, you can refer to your preferred IDE documentation to see how to import and compile Maven-based
projects.

Alternatively, if you have Maven installed, you can compile and install the ClearBlade Java SDK to your local machine
using the following command from the ClearBlade Java SDK repository root:

```
mvn install
```

The command above will install the currently checked-out version.

#### Dependencies

Check [pom.xml](pom.xml) for reference.

# Examples

- [User auth example](src/main/java/com/clearblade/examples/UserAuthExample.java).
- [Device auth example](src/main/java/com/clearblade/examples/DeviceAuthExample.java).
- [Legacy example](src/main/java/com/clearblade/examples/MqttClientJava.java).

# API references

## Initialization

Initialization is the first and crucial step in using the ClearBlade Java
API for your application. You will not be able to access any ClearBlade Platform features without initialization.

You must import the following packages in your Java file:

```java
import com.clearblade.java.api.ClearBlade;
```

```java
import com.clearblade.java.api.InitCallback;
```

There are two ways to initialize the SDK:

#### Without options

```java
String SYSTEM_KEY = "your_systemkey";
String SYSTEM_SECRET = "your_systemsecret";
InitCallback initCallback = new InitCallback(){
    @Override
    public void done(boolean results){
	    //initialization successful
	}
	@Override
	public void error(ClearBladeException exception){
	   //initialization failed, given a ClearBladeException with the cause
        Log.i("Failed init", "holy cow!!" + exception.getLocalizedMessage());
    }
};
clearBlade.initialize(SYSTEM_KEY, SYSTEM_SECRET, initCallback);
```

#### With options

Use the `com.clearblade.java.api.InitOptions` class to construct your options object:

```java
import com.clearblade.java.api.InitOptions;
InitOptions initOptions = new InitOptions()
    // URL of the platform to use (default: https://platform.clearblade.com)
    .setPlatformUrl(String)
    // URL of the messaging backend to use (default: tcp://messaging.clearblade.com:1883)
    .setMessagingUrl(String)
    // Authorization method to use (default: com.clearblade.java.api.auth.AnonAuth)
    .setAuth(com.clearblade.java.api.auth.Auth)
    // Enable internal ClearBlade JDK logging (default: false)
    .setEnableLogging(Boolean)
    // Timeout in milliseconds for API calls (default: 30000)
    .setCallTimeout(Integer)
    // Allow connecting to a platform server without a signed SSL certificate
    .setAllowUntrusted(Boolean);
```

After configuring your options object, you can use it in your initialize call:

```java
String SYSTEM_KEY = "your_systemkey";
String SYSTEM_SECRET = "your_systemsecret";
InitCallback initCallback = new InitCallback(){
    @Override
    public void done(boolean results){
	// initialization successful
    }
    @Override
    public void error(ClearBladeException exception){
	// initialization failed
    }
};
ClearBlade.initialize(SYSTEM_KEY, SYSTEM_SECRET, initOptions, initCallback);
```

## Authentication methods

Authentication is handled by implementing the `com.clearblade.java.api.auth.Auth`
interface to the `com.clearblade.java.api.InitOptions/setAuth` method. It uses a `com.clearblade.java.api.auth.AnonAuth` instance by default.

Here's a small snippet that authenticates to the Platform using user authentication:

```java
import com.clearblade.java.api.ClearBlade;
import com.clearblade.java.api.InitOptions;
import com.clearblade.java.api.auth.UserAuth;
InitOptions initOptions = new InitOptions()
    .setAuth(new UserAuth("YOUR EMAIL", "YOUR PASSWORD");
ClearBlade.initialize("YOUR SYSTEM KEY", "YOUR SYSTEM SECRET", initOptions, ...);
```
Check the `com.clearblade.java.api.auth` package for more authentication methods.

## Code
The ClearBlade Java API allows executing a code service from your Java application on the Platform.
**Please ensure that you have initialized and authenticated with the ClearBlade Platform before using the Code API.**
You must import the following packages to use the Code API:
```import com.clearblade.java.api.Code;```
```import com.clearblade.java.api.CodeCallback;```

#### Code service without parameters
A code service which does not take any parameters can be executed as follows:
```java
String serviceName = "yourServiceName";
CodeCallback codeCallback = new CodeCallback() {
	@Override
	public void done(JsonObject response) {
	    // Code Service executed successfully
		Log.i("codeResponse", response.toString());
	}
	@Override
	public void error(ClearBladeException exception) {
	    // Code Service execution failed
		Log.i("codeResponse", exception.getMessage());
	}
};
Code codeService = new Code(serviceName);
codeService.executeWithoutParams(codeCallback);
```
#### Code service with parameters
A JSON object of parameters needs to be passed to the ```code``` class constructor with the service name:
```java
String serviceName = "yourServiceName";
String parameters = "{\"param1\":\"value1\"}";
JsonObject parameterJsonObject = new JsonParser().parse(parameters).getAsJsonObject();
CodeCallback codeCallback = new CodeCallback() {
	@Override
	public void done(JsonObject response) {
	    // Code Service executed successfully
		Log.i("codeResponse", response.toString());
	}
	@Override
	public void error(ClearBladeException exception) {
	    // Code Service execution failed
		Log.i("codeResponse", exception.getMessage());
	}
};
Code codeService = new Code(serviceName, parameterJsonObject);
codeService.executeWithParams(codeCallback);
```
## Data
With the ClearBlade Java API, a developer can use the ```query```, ```item```, and ```collection``` objects to manipulate data on the ClearBlade Platform.
Import the following packages:
-```import com.clearblade.java.api.Collection;```
-```import com.clearblade.java.api.Query;```
-```import com.clearblade.java.api.Item;```
-```import com.clearblade.java.api.DataCallback;```

## Query
Create a new ```query``` object:
```java
String collectionID = "yourCollectionID";
Query query = new Query(collectionID);
```
#### query.EqualTo(String field, Object value)
```java
/**
	 * Creates an equality clause in the query object
*/
	 query.equalTo('name', 'John');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
/* Will only match if an item has an attribute 'name' that is equal to 'John' */
```

#### query.notEqual(String field, Object value)

```java
/**
	 * Creates a non-equality clause in the query object
*/
	 query.notEqual('name', 'John');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
/* Will only match if an item has an attribute 'name' that is not equal to 'John' */
```

#### query.greaterThan(String field, Object value)

```java
/**
	 * Creates a greater than clause in the query object
*/
	 query.greaterThan('age', '18');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
	 /* Will return all the items that are greater than age 18 if present*/
```

#### query.greaterThanEqualTo(String field, Object value)

```java
/**
	 * Creates a greater than or equal to clause in the query object
*/
	 query.greaterThanEqualTo('age', '18');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
	 /* Will return all the items that are greater than equal to age 18 if present*/
```

#### query.lessThan(String field, Object value)

```java
/**
	 * Creates a less than clause in the query object
*/
	 query.lessThan('age', '18');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
	 /* Will return all the items that are less than age 18 if present*/
```

#### query.lessThanEqualTo(String field, Object value)

```java
/**
	 * Creates a less than equal to clause in the query object
*/
	 query.lessThanEqualTo('age', '18');
	 query.fetch(new DataCallback{
	    public void done(QueryResponse resp){
	       //your logic here
	    }
	 });
	 /* Will return all the items that are less than equal to age 18 if present*/
```

#### query.update(final DataCallback callback)

```java
	 /* Call an update on all items matching the query criteria to conform to the changes that have been added via the addChange method */
	 	query.equalTo("name", "John");
	 	query.addChange("name", "Johan");
	 	query.update( new DataCallback() {
	 		@Override
	 		public void done(Item[] response) {
	 		    // Query successful
	 		}
	 		@Override
	 		public void error(ClearBladeException exception) {
	 			// Query unsuccessful
	 		}
	 	});
```

#### query.remove(final DataCallback callback)

```java
	 /* Removes all items matching the query criteria within a collection */
	 	query.equalTo("name", "John");
	 	query.remove( new DataCallback() {
	 		@Override
	 		public void done(Item[] response) {
	 		     // Query successful
	 		}
	 		@Override
	 		public void error(ClearBladeException exception) {
	 			// Query unsuccessful
	 		}
	 	});
```

The page size and page number of the results to be returned can be set using ```query.setPageSize(int pageSize)``` and ```query.setPageNum(int pageNum)```.

## Collections

The ```Collection``` class contains functions to **fetch (GET)**, **update (PUT)**, **create (POST)**, and **remove (DELETE)** a collection using the REST API.
A collection object needs to be created first:
```java
String collectionID = "yourCollectionID";
Collection collection = new Collection(collectionID);
```

#### collection.fetch(Query query, final DataCallback callback)

```java
/**
	 * Gets all items that match query criteria from the Platform in the Cloud.
	 * Retrieved items will be stored locally in the collection.</p>
	 * Overrides previously stored items*</strong>
	 * Runs in its asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in the callback.error() if the collection was empty
	 */
Query query = new Query();
query.equalTo("height", 105);
collection.fetch(query, new DataCallback() {
    @Override
    public void done(QueryResponse response) {
        //Success
    }
    @Override
    public void error(ClearBladeException exception) {
        //Failure
    }
});
```

#### collection.fetchAll(final DataCallback callback)

```java
/**
	 * Gets all items that are saved in the collection in the Cloud.
	 * Retrieved items will be stored locally in the collection.</p>
	 * Overrides previously stored items*</strong>
	 * Runs in its asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in the callback.error() if the collection was empty
	 */
collection.fetchAll(new DataCallback() {
    @Override
    public void done(QueryResponse response) {
        //Success
    }
    @Override
    public void error(ClearBladeException exception) {
        //Failure
    }
});
```

#### collection.update(final DataCallback callback)

```java
Query query = new Query(collectionID);
query.equalTo("name", "John");
query.addChange("name", "Johan");
collection.update(new DataCallback() {
    @Override
    public void done(Item[] response) {
        // Query successful
    }
    @Override
    public void error(ClearBladeException exception) {
        // Query unsuccessful
    }
});
```

#### collection.create(String columns, final DataCallback callback)

```java
String column = "{\"columnName\":\"newColumn\"}";
collection.create(column, new DataCallback() {
    @Override
    public void done(Item[] response) {
        // Query successful
    }
    @Override
    public void error(ClearBladeException exception) {
        // Query unsuccessful
    }
});
```

#### collection.remove(DataCallback callback)

```java
/**
	 * Deletes all items that are saved in the collection in the Cloud synchronously.
	 * Deleted items will be stored locally in the collection.</p>
	 * Overrides previously stored items*</strong>
	 * Runs in its asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in the callback error function
	 */
collection.remove(new DataCallback() {
    @Override
    public void done(QueryResponse response) {
        //Success
    }
    @Override
    public void error(ClearBladeException exception) {
        //Failure
    }
});
```

## Messaging

The Messaging API is used to initialize, connect, and communicate with the ClearBlade MQTT broker to publish messages, subscribe, and unsubscribe to and from topics.

**Please ensure that you have initialized and authenticated with the ClearBlade Platform before using the Messaging API. This is important because the ClearBlade MQTT broker requires the authentication token to establish a successful connection. This authentication token can only be obtained by initializing and authenticating with the ClearBlade Platform**

You must import the following packages using the Messaging API:
```import com.clearblade.java.api.MqttClient;```
```import com.clearblade.java.api.MessageCallback;```

### Initialize and connect
The first step is to create a new ```Message``` object by passing the client ID and messaging QoS (optional). The ```Message``` constructor will then initialize and connect with the MQTT broker.
```java
String clientID = “ClearBladeJavaTest”;
MqttClient mqttClient = new MqttClient(clientID); // QoS = 0 Default
```
OR
```java
int qos = 1; // QoS can be 0,1 or 2
String clientID = “ClearBladeJavaTest”;
MqttClient mqttClient = new MqttClient(clientID, qos);
```
OR
```java
int qos = 1; // QoS can be 0,1 or 2
String clientID = “ClearBladeJavaTest”;
boolean autoReconnect = false;
MqttClient mqttClient = new MqttClient(clientID, qos, autoReconnect);
// maxInflight = 10 Default
```
OR
```java
int qos = 1; // QoS can be 0,1 or 2
String clientID = “ClearBladeJavaTest”;
boolean autoReconnect = false;
int maxInflight = 100;
MqttClient mqttClient = new MqttClient(clientID, qos, autoReconnect, maxInflight);
```

After the successful connection, you can publish, subscribe, unsubscribe, or disconnect using the ```Message``` object.

### Publish

The publish function takes a topic and message of type ```string``` and publishes them to the MQTT broker.
```java
String topic = "yourTopic";
String message = "yourMessage";
mqttClient.publish(topic, message);
```

### Subscribe

The subscribe function takes a topic of type ```string``` and a callback to handle the arrived messages.
```java
String topic = "topicToSubscribe";
MessageCallback messageCallback = new MessageCallback() {
	@Override
	public void done(String topic, String messageString) {
		//Message arrived on the subscribed topic
	}
};
mqttClient.subscribe(topic, messageCallback);
```

### Unsubscribe

The unsubscribe function takes a topic of type ```string``.
```java
String topic = "topicToUnsubscribe";
mqttClient.unsubscribe(topic);
```

### Disconnect
The disconnect function is used to disconnect from the MQTT broker. **This does not disconnect the user from the ClearBlade Platform. User logout needs to be called separately.**
```java
mqttClient.disconnect();
```
