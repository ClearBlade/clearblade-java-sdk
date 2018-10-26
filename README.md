# API References
## Authenticating
Authentication is the very first and crucial step in using the ClearBlade Java API for your application. You will not be able to access any features of the ClearBlade platform without Authentication. 

You will need to import the following packages in your java file:  
``` import com.clearblade.java.api.ClearBlade; ```  
```import com.clearblade.java.api.InitCallback;```

There are two ways to authenticate to the ClearBlade platform:

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

ClearBlade clearBlade = new ClearBlade();
clearBlade.initialize(SYSTEM_KEY, SYSTEM_SECRET, initCallback);
```

#### With Options
```java
String SYSTEM_KEY = "your_systemkey";
String SYSTEM_SECRET = "your_systemsecret";
HashMap<String, Object> initOptions = new HashMap<String, Object>();

/**Available init options:
	 * 	email - String to register or log-in as specific user (required if password is given) Default - null
	 * 	password - password String for given user (required if email is given) Default - null
	 * 	platformURL - Custom URL for the platform Default - https://platform.clearblade.com
	 * 	messagingURL - Custom Messaging URL Default - tcp://messaging.clearblade.com:1883
	 * 	registerUser - Boolean to tell if you'd like to attempt registering the given user Default - false
	 * 	logging - Boolean to enable ClearBlade Internal API logging Default - false
	 * 	callTimeout - Int number of milliseconds for call timeouts Default - 30000 (30 seconds)
	 *  allowUntrusted - Boolean to connect to a platform server without a signed SSL certificate Default - false
*/

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

initOptions.put("platformURL", "https://yourURL.com");
initOptions.put("messagingURL", "tcp://yourURL:1883");

ClearBlade clearBlade = new ClearBlade();
clearBlade.initialize(SYSTEM_KEY, SYSTEM_SECRET, initOptions, initCallback);
```

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
```import com.clearblade.java.api.Message;```  
```import com.clearblade.java.api.MessageCallback;```

### Initialize and Connect  
The first step is to create a new ```Message``` object by passing the client ID and messaging QoS (optional). The ```Message``` constructor will then initialize and connect with the MQTT Broker.
```java
String clientID = “ClearBladeJavaTest”; 
Message message = new Message(clientID); // QoS = 0 Default
```
OR
```java
int qos = 1; // QoS can be 0,1 or 2
String clientID = “ClearBladeJavaTest”;  
Message message = new Message(clientID, qos);
```

After the connection is successful, you can publish, subscribe, unsubscribe or disconnect using the ```Message``` object. 

### Publish
The publish function takes a topic and message of type ```String``` and publishes to the MQTT Broker.
```java
String topic = "yourTopic";
String message = "yourMessage";
message.publish(topic, message);
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
message.subscribe(topic, messageCallback);
```

### Unsubscribe
The unsubscribe function takes a topic of type ```String``.
```java
String topic = "topicToUnsubscribe";
message.unsubscribe(topic);
```

### Disconnect
The disconnect function is used to disconnect from the MQTT Broker. **Note that this does not disconnect the user from the ClearBlade platform. User logout needs to be called separately.**
```java
message.disconnect();
```
# Setup

#### Dependencies
The ClearBlade Java API requires three JAR files that should be copied to your project’s ```/libs``` folder:  
-```ClearBlade_Java_API.jar```  
-```org.eclipse.paho.client.mqttv3-1.0.2.jar```  
-```gson-2.3.jar```

# Example

Here's an example of a ClearBlade Java Client that initializes with the ClearBlade platform, connects to the MQTT broker and publishes and subscribes to messages. Then it disconnects from the MQTT Broker and logs out the user.  
```java
package com.example.client;

import java.util.HashMap;

import com.clearblade.java.api.*;
import com.clearblade.java.api.internal.*;

public class MQTTClientJava {

	private static Message message;
	private static boolean isInit = false;
	
	public static void main(String[] args) {
		
		initClearBlade();
		if (isInit) {
			
			connectToMQTT();
			
			try {
				Thread.sleep(2000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			subscribe("hello");
			
			try {
				Thread.sleep(5000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			publish("hello", "this is a test");
			
			try {
				Thread.sleep(5000);
			} catch(InterruptedException ex) {
				ex.getMessage();
			}
			
			message.disconnect();
			
			logoutUser();
		}
	}
	
	private static void initClearBlade() {
		
		InitCallback initCallback = new InitCallback() {
			
			@Override
			public void done(boolean results) {
					
				System.out.println("ClearBlade platform initialized");
				isInit = true;
			}
			
			@Override
			public void error(ClearBladeException error) {
				
				isInit = false;
				String message = error.getMessage();
				System.out.println(message);
			}
		};
		
		String systemKey = "yourSystemKey";
		String systemSecret = "yourSystemSecret";
		String userEmail = "example@clearblade.com";
		String userPassword = "password";
		String platformURL = "https://example.clearblade.com";
		String messagingURL = "tcp://example.clearblade.com:1883";
		
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put("email", userEmail);
		options.put("password", userPassword);
		options.put("platformURL", platformURL);
		options.put("messagingURL", messagingURL);
		
		ClearBlade.initialize(systemKey, systemSecret, options, initCallback);
	}
	
	private static void connectToMQTT() {
		
		message = new Message("clientID-test", 1);
	}
	
	private static void subscribe(String topic) {
		
		MessageCallback messageCallback = new MessageCallback() {
			
			@Override
			public void done(String topic, String message){
				
				System.out.println("Topic: " + topic +" Message received: " + message);
			}
			
			@Override
			public void error(ClearBladeException exception) {
				
				String message = exception.getLocalizedMessage();
				System.out.println("CB Subscribe Exception: " + message);
			}
		};
		
		message.subscribe(topic, messageCallback);
	}
	
	private static void publish(String topic, String payload) {
		
		message.publish(topic, payload);
	}
	
	private static void logoutUser() {
		
		User currentUser = ClearBlade.getCurrentUser();
		currentUser.logout(new InitCallback() {

			@Override
			public void done(boolean results) {
				
				System.out.println("User logged out");
			}
			@Override
			public void error(ClearBladeException exception) {
				System.out.println("Logout failed " + exception.getMessage());
			}
			
		});
	}

}
```

# JavaDoc

The Javadoc for the Java API can be found at https://docs.clearblade.com/v/3/static/javaapi/index.html

# QuickStart

#### Installation
Java clients using the ClearBlade java SDK can be written in any idea of your choice.  Popular recommended IDEs include
- Eclipse
- IntelliJ
- NetBeans 


#### Dependencies
The ClearBlade Java API requires three JAR files that should be copied to your project’s ```/libs``` folder:  
-```ClearBlade_Java_API.jar```  
-```org.eclipse.paho.client.mqttv3-1.0.2.jar```  
-```gson-2.3.jar```

