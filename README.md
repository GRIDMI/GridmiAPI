# GridmiAPI

Very simple and powerful work with RestAPI for Android.

## Mission

This library will allow you to easily fulfill server requests. You do not need to think: about restoring the URI of your request, about threads, about the results. You simply fulfill the request and get a response. You can also interrupt the request, as the task instance is the most common `java.lang.Thread()`.

## Install

Add the dependency to your *build.gradle* project file:

`implementation 'com.gridmi.api:GridmiAPI:1.0.14'`

## Start

The first thing you need to do is initialize the library. It is easy to do. You must call the static method of the class `GridmiAPI.init()`.

~~~
GridmiAPI.init("http://gridmi.com/", 8000, JSONObject.class);
~~~

- http://gridmi.com/ - path to the handler on the server;
- 8000 - connection timeout;
- JSONObject.class - class to which server response should be cast;

## First server request

To send a request, you need to call the static method of the class `GridmiAPI.onRequest(Activity, GridmiAPI.Request, GridmiAPI.Handler.OUT)` and pass the necessary arguments. You will get an instance of `Thread.class`, you can start the execution of the request using `.start() `method. The simplest query using this library can be formed as follows:

~~~
GridmiAPI.onRequest(this, new GridmiAPI.Request("news/get"), new GridmiAPI.Handler.OUT() {
        
    /** These two methods are called
     * in the MAIN THREAD **/
    
    @Override
    protected void onSuccess(GridmiAPI.Response response) {
        /* TODO
        * The request was sent successfully and
        * the response was cast to the type you specified 
        * */
    }

    @Override
    protected void onFailed(Exception exception) {
        /* TODO
        * An error occurred while establishing a connection,
        * converting the response, or if something went wrong.
        * */
    }
    
}).start();
~~~

## Get and parse the response

After the request has been successfully completed. The library calls the main thread from the activity that was passed to the operation start handler. Let's try to extract some data:

~~~
@Override
protected void onSuccess(GridmiAPI.Response response) {
    try {

        // Get JSON object from response
        JSONObject jsonObject = (JSONObject) response.getData();

        // Check result of the operation
        if (jsonObject.getBoolean("result")) {
            
            // Get string of the object and set it to EditText
            ((EditText) findViewById(R.id.result)).setText(jsonObject.getString("data"));
            return;
            
        }

        // Create new exception
        throw new Exception(jsonObject.getString("detail"));

    } catch (Exception exception) {
        this.onFailed(exception);
    }
}
~~~

It should be understood that the request will be successful if the response body was successfully cast to the type you specified. Here was the initialization of the library, casting the response to a type - JSONObject.class.

### Type of proposed response

You can safely change the type of the intended server response to the instance of each request. It is very easy to do:

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("news/get");
    
// Set response of body
request.setResponseBody(String.class);
    
// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...}).start();
~~~

## Set request headers

After the request instance is initialized, you can set an HTTP header to it. It's simple:

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("POST", "order/create");
    
// Header like String
GridmiAPI.Header type = request.addHeader("type", "programmer");

// Header like Boolean
GridmiAPI.Header.Boolean marital = request.addHeader("marital", false);

// Header like Integer
GridmiAPI.Header.Int age = request.addHeader("age", 23);

// Header like Double
GridmiAPI.Header.Double latitude = request.addHeader("latitude", 0.000);
GridmiAPI.Header.Double longitude = request.addHeader("longitude", 0.000);

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

After the header has been added, you can keep it down by behavior when executing queries. Take for example the `type` header.

### Header behavior management

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("POST", "order/create");

// Header like String
GridmiAPI.Header type = request.addHeader("type", "new");
    
// Set and get the key (yourself)
type.setKey(type.getKey());
    
// Set and get the value (yourself)
type.setValue(type.getValue());
    
// Do not send on subsequent requests
type.setConnected(false);

// Submit on subsequent requests
type.setConnected(true);
    
// Check if header is sent
if (type.isConnected());

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

### Additionally

After you set the header value, the knowledge setting method returns the set value. This is for convenience. For example:

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("POST", "order/create");

// Header of request
GridmiAPI.Header.Int lastId = request.addHeader("lastId", 0);
    
// Check ID item from collection
if (lastId.setConnected(!collection.isEmpty())) lastId.setValue(collection.get(collection.size() - 1).id);

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

#### *Important!*

If you use wrapper classes for the header, such as `GridmiAPI.Header.Byte`,` GridmiAPI.Header.Short`, `GridmiAPI.Header.Char`,` GridmiAPI.Header.Int`, `GridmiAPI.Header.Float`, `GridmiAPI.Header.Long`,` GridmiAPI.Header.Double`, `GridmiAPI.Header.Boolean`, it is important to understand that these classes are inherited from the parent class` GridmiAPI.Header`, then the methods are used to set the value of the wrapper class `.setValue(primitive)` and `.setValue(STRING)`, while where a string is passed as a method argument, the value can return` NULL`, since the value was not cast to this primitive type and the value remains the same.

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("POST", "order/create");

// Header of request
GridmiAPI.Header.Int id = request.addHeader("id", 0);
    
// Set and check actual data of header
if (id.setValue("1") == null) Log.d("tag", "FALSE, string '1' can convert to int");

// Set and check actual data of header
if (id.setValue("1-") == null) Log.d("tag", "TRUE, string '1-' can't convert to int.");

// Set and check actual data of header
if (id.setValue("-1") == null) Log.d("tag", "FALSE, string '-1' can convert to int.");

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

It’s good practice to pass the primitive according to your copy of the header:

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("GET", "order/get");

// Header INT of request
GridmiAPI.Header.Int id = request.addHeader("id", 0);
    
id.setValue(1);
id.setValue(2);
id.setValue(3); // etc
    
// Header DOABLE of request
GridmiAPI.Header.Double price = request.addHeader("price", 0.00);
    
price.setValue(1.00);
price.setValue(2.00);
price.setValue(3.00); // etc

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

## Set request parameters

You can easily initialize GET parameters and control their behavior. The parameters are ABSOLUTELY identical to the headers, only the `GridmiAPI.Param` class is used.

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("GET", "order/get");

// Header INT of request
GridmiAPI.Param.Int id = request.addParam("id", 10);

// Send request to the server
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...});
~~~

See the rest of the information for example headers. The behavior of the headers and parameters is identical.

## Set request body

In order to set the request body, you just need to call your request method `.setBody(ENUM (String, JSONObject, JSONArray, GridmiAPI.Multipart))` and send the request.

~~~
try {

    // Create new instance of request
    GridmiAPI.Request request = new GridmiAPI.Request("POST", "order/create");

    // Create body of request
    JSONObject body = new JSONObject();

    // Put data to JSONObject
    body.put("firstName", "Dmitrii");
    body.put("lastName", "Grigorev");
    body.put("age", 23);
    
    // Put your JSONObject to body of request
    request.setBody(body);

    // Send request to the server
    GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...}).start();

} catch (JSONException exception) {
    // JSON exception
}
~~~

## Transfer file to server

This library will allow you to transfer the file to the server very simply. You only need to initialize the `GridmiAPI.Multipart` object and add the component of the request body, then add` GridmiAPI.Multipart` to your request instance `GridmiAPI.Request` by calling the `.setBody(GridmiAPI.Multipart)` method. When we implicitly call an activity, for example, `Choiser` and as a result,` Uri` is present in `Intent.getData()`.

~~~
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    try {
        
        // User must select file
        if (data == null || data.getData() == null) throw new Exception("Required to select data!");

        // Create new instance of request
        GridmiAPI.Request request = new GridmiAPI.Request("POST", "photo/add");
        
        // Create multipart of request
        GridmiAPI.Multipart multipart = new GridmiAPI.Multipart(getContentResolver());

        // Append string part to the request body
        GridmiAPI.Multipart.Data email = multipart.appendData("email", "example@gridmi.com");
        
        // Append file part to the request body
        GridmiAPI.Multipart.Data file = multipart.appendData("file", data.getData());

        // Send request to the server
        GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...}).start();

    } catch (Exception exception) {
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }
}
~~~

### Controlling behavior of components

You can easily add as well as remove components of your `GridmiAPI.Multipart` instance.

~~~
// Create new instance of request
GridmiAPI.Request request = new GridmiAPI.Request("POST", "user/registration");

// Create multipart of request
GridmiAPI.Multipart multipart = new GridmiAPI.Multipart(getContentResolver());
    
// Set body instance as request body
request.setBody(multipart);

// Append string part to the request body
GridmiAPI.Multipart.Data login = multipart.appendData("login", "example@gridmi.com");

// Append string part to the request body
GridmiAPI.Multipart.Data password = multipart.appendData("password", "123456");
    
// Delete the password from the request body instance
multipart.removeData(password);

// Send request to the server, ONLY `login` field
GridmiAPI.onRequest(this, request, new GridmiAPI.Handler.OUT() {...}).start();
~~~

## Casting a response to type

In case of a successful request to the server and receiving the response, the library will try to convert the response to a specific type.

#### Supported response conversion types

- InputStream.class
- JSONArray.class
- JSONObject.class
- String.class

### An example of casting to an input stream

~~~
// Send request to the server
GridmiAPI.onRequest(this, new GridmiAPI.Request("GET", "stream/get"), new GridmiAPI.Handler.OUT() {
    
    @Override
    protected void onSuccess(GridmiAPI.Response response) {
        try {
            
            // Get InputStream and close it
            ((InputStream) response.getData()).close();
            
        } catch (Exception exception) {
            this.onFailed(exception);
        }
    }

    @Override
    protected void onFailed(Exception exception) {
        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
    }
    
}).start();
~~~

## Thread UI & Thread Background Sync

There is a need to synchronize two threads so that the background thread continues to work only when the main thread has completed its work. To do this, you need to pass an additional argument.

~~~
// Create request to the server
GridmiAPI.Request request = new GridmiAPI.Request("profile/get");

@Override
public void run() {
    try {
        
        // Work in a loop until the thread is't interrupted
        while (Thread.currentThread().isInterrupted()) synchronized (this) {
            
            // Go to MAIN Thread
            GridmiAPI.onRequest(this, MainActivity.this, request, new GridmiAPI.Handler.OUT() {
                
                /**
                 * After executing onSuccess() or onFailed()
                 * Go to Background Thread
                 * */
                
                @Override
                protected void onSuccess(GridmiAPI.Response response) {
                }

                @Override
                protected void onFailed(Exception exception) {
                }

            }).start();

        }
        
        // Sleep 1 second
        Thread.sleep(1000);
        
    } catch (Exception exception) {
        // Exception
    }
}
~~~

## Synchronous server request

When there is a need to execute a request outside the main thread, you can directly execute a request directly from that background thread.

~~~
// Create request to the server
Object request = GridmiAPI.onRequest(new GridmiAPI.Request("profile/get"));
~~~

#### *Important!*

When you execute a non-asynchronous request, the result of the operation must be checked manually.

~~~
// Create request to the server
Object request = GridmiAPI.onRequest(new GridmiAPI.Request("profile/get"));

// Check result of the operation
Object result = request instanceof GridmiAPI.Response ? ((GridmiAPI.Response) request).getData() : request;

// If request is successful then `request` will be GridmiAPI.Response
// otherwise `request` will be Exception
Log.d("tag", result.toString());
~~~

## GridmiAPI.Request.class

This table describes the constructors of this class.

|Version|Description|
|:----:|----|
|String|Then pass your `endpoint`. = *GET "STATIC + endpoint"*|
|String, String|Then pass the method and your `endpoint`. = *METHOD "STATIC + endpoint"*|
|String, String, boolean|Here you pass your method, **URL** and `boolean` - do you need to temporarily replace the static definition. = *METHOD IF boolean "URL" ELSE "STATIC + URL"*|
|String, Strign, Class|Then pass the method, `endpoint` and` Class`, which tells the library what type the server response should be cast to. = *METHOD "STATIC + endpoint"*|

This table describes the methods for flexible work with queries.

|Method|Arguments|Return|Description|
|:----:|---------|:----:|-----------|
|setTimeOut()|int|int|Sets the connection timeout.|
|getLastURL()|-|String / **NULL**|Returns the last initialized URL.|
|addHeader()|(String, `ENUM`(boolean, short, char, float, int, long, double, boolean, String))|GridmiAPI.Header -> `ENUM`(Byte, Short, Char, Float, Int, Long, Double, Boolean)|Add a header to the request.|
|removeHeader()|GridmiAPI.Header|boolean|Delete a header from the request.|
|clearHeaders()|-|void|Remove all headers from the request.|
|addParam()|(String, `ENUM`(boolean, short, char, float, int, long, double, boolean, String))|GridmiAPI.Param -> `ENUM`(Byte, Short, Char, Float, Int, Long, Double, Boolean)|Add a param to the request.|
|removeParam()|GridmiAPI.Param|boolen|Delete a param from the request.|
|clearParams()|-|void|Remove all params from the request.|
|setBody()|(`ENUM`(String, JSONObject, JSONArray, GridmiAPI.Multipart), boolean(setDefaultHeader))|void|Sets the request body of the current instance.|
|removeBody()|-|void|Delete a body of the request.|
|setResponseBody()|Class|Class|Set a class for conversion when processing a response.|
|getTimeOut()|-|int|Allows you to get the timeOut request|
|setMethod()|String|String|Sets the connection method|
|setURL()|String|String|Sets the connection address|
|setConnectStaticHeaders()|boolean|boolean|Sets the ability to add static headers to the request|
|setConnectStaticParams()|boolean|boolean|Sets the ability to add static params to the request|

## GridmiAPI.StaticManager.class

Static class for managing static data (`headers` and `parameters `).

|Method|Argiments|Return|Description|
|:----:|:-------:|:----:|:---------:|
|addHeader()|(Header) or (String, String)|Instance of header if succeess added else NULL|Allows you to add a static header|
|addParam()|(Param) or (String, String)|Instance of param if succeess added else NULL|Allows you to add a static param|
|removeHeader()|-|TRUE if removed or FALSE if not removed|Allows you to remove a static header|
|removeParam()|-|TRUE if removed or FALSE if not removed|Allows you to remove a static param|
|clearHeaders()|-|VOID|Removes all static headers.|
|clearParams()|-|VOID|Removes all static params.|

## License

Copyright 2019 GridmiAPI

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
