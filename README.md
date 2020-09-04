# QuickStart

The ClearBlade Java SDK uses [Maven](https://maven.apache.org/) to manage the project and its dependencies. Since
most IDEs support Maven, you can refer to your preferred IDE documentation to see how to import and compile Maven-based
projects.

Alternatively, if you have Maven installed, you can compile and install the ClearBlade Java SDK to your local machine
using the following command from the root of the ClearBlade Java SDK repository:

```
mvn install
```

The command above will install the currently checked out version.

#### Dependencies

Check [pom.xml](pom.xml) for reference.

# Examples

- [User auth example](src/main/java/com/clearblade/examples/UserAuthExample.java).
- [Device auth example](src/main/java/com/clearblade/examples/DeviceAuthExample.java).
- [Legacy example](src/main/java/com/clearblade/examples/MQTTClientJava.java).

# API References

## Initialization

Initialization is the very first and crucial step in using the ClearBlade Java
API for your application. You will not be able to access any features of the
ClearBlade platform without initialization.

You will need to import the following packages in your java file:

```java
import com.clearblade.java.api.ClearBlade;
```

```java
import com.clearblade.java.api.InitCallback;
```

There are two ways to initialize the SDK:

#### Without Options

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

#### With Options

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
    .setAllowUntrusted(Boolean)
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

Authentication is handled by passing an implementation of the `com.clearblade.java.api.auth.Auth`
interface to the `com.clearblade.java.api.InitOptions/setAuth` method. By default,
it uses a `com.clearblade.java.api.auth.AnonAuth` instance.

Here's a small snippet that authenticates to the platform using user authentication:

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

The ClearBlade Java API allows executing a Code Service on the platform from your Java application.

**Please make sure that you have initialized and authenticated with the ClearBlade platform prior to using the Code API.**

You need to import the following packages to use the Code API:
```import com.clearblade.java.api.Code;```
```import com.clearblade.java.api.CodeCallback;```

#### Code Service Without Parameters

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

#### Code Service With Parameters

A Json Object of parameters needs to be passes to the ```Code``` class constructor along with the service name:
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

With the ClearBlade Java API, a developer can use the ```Query, Item``` and ```Collection``` objects to manipulate data on the ClearBlade platform.
Import the following packages:
-```import com.clearblade.java.api.Collection;```
-```import com.clearblade.java.api.Query;```
-```import com.clearblade.java.api.Item;```
-```import com.clearblade.java.api.DataCallback;```

## Query

Create a new ```Query``` object:
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
	 /* Removes on all items matching the query criteria within a Collection */

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

The page size and page number of the results to be returned can be set by using ```query.setPageSize(int pageSize)``` and ```query.setPageNum(int pageNum)```.

## Collections

The ```Collection``` class contains functions to **fetch (GET)**, **update (PUT)**, **create (POST)** and **remove (DELETE)** a collection using the REST API.
A collection object needs to be created first:
```java
String collectionID = "yourCollectionID";
Collection collection = new Collection(collectionID);
```

#### collection.fetch(Query query, final DataCallback callback)

```java
/**
	 * Gets all Items that match Query criteria from the platform in the Cloud.
	 * Retrieved Items will be stored locally in the Collection.</p>
	 * Overrides previously stored Items*</strong>
	 * Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in callback.error() if the collection was empty
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
	 * Gets all Items that are saved in the collection in the Cloud.
	 * Retrieved Items will be stored locally in the Collection.</p>
	 * Overrides previously stored Items*</strong>
	 * Runs in its own asynchronous task*</strong>
	 * @throws ClearBladeException will be returned in callback.error() if the collection was empty
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
	 * Deletes all Items that are saved in the collection in the Cloud synchronously.
	 * Deleted Items will be stored locally in the Collection.</p>
	 * Overrides previously stored Items*</strong>
	 * Runs in its own asynchronous task*</strong>
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

The Messaging API is used initialize, connect and communicate with the ClearBlade MQTT Broker for publishing messages, subscribing, unsubscribing to and from topics and disconnect.

**Please make sure that you have initialized and authenticated with the ClearBlade platform prior to using the Messaging API. This is important because the ClearBlade MQTT Broker requires the authentication token to establish a successful connection. This authentication token can only be obtained by initializing and authenticaing with the ClearBlade platform**

You will need to import the following packages for using the Messaging API:
```import com.clearblade.java.api.MQTTClient;```
```import com.clearblade.java.api.MessageCallback;```

### Initialize and Connect

The first step is to create a new ```Message``` object by passing the client ID and messaging QoS (optional). The ```Message``` constructor will then initialize and connect with the MQTT Broker.
```java
String clientID = “ClearBladeJavaTest”;
MQTTClient mqttClient = new MQTTClient(clientID); // QoS = 0 Default
```
OR
```java
int qos = 1; // QoS can be 0,1 or 2
String clientID = “ClearBladeJavaTest”;
MQTTClient mqttClient = new MQTTClient(clientID, qos);
```

After the connection is successful, you can publish, subscribe, unsubscribe or disconnect using the ```Message``` object.

### Publish

The publish function takes a topic and message of type ```String``` and publishes to the MQTT Broker.
```java
String topic = "yourTopic";
String message = "yourMessage";
mqttClient.publish(topic, message);
```

### Subscribe

The subscribe function takes a topic of type ```String``` and a callback to handle the arrived messages.
```java
String topic = "topicToSubscribe";
MessageCallback messageCallback = new MessageCallback() {
	@Override
	public void done(String topic, String messageString) {
		//Message arrived on subscribed topic
	}
};
mqttClient.subscribe(topic, messageCallback);
```

### Unsubscribe

The unsubscribe function takes a topic of type ```String``.
```java
String topic = "topicToUnsubscribe";
mqttClient.unsubscribe(topic);
```

### Disconnect

The disconnect function is used to disconnect from the MQTT Broker. **Note that this does not disconnect the user from the ClearBlade platform. User logout needs to be called separately.**
```java
mqttClient.disconnect();
```

# JavaDoc

The Javadoc for the Java API can be found [here](https://docs.clearblade.com/v/3/static/javaapi/index.html).

