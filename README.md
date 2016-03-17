# secucard connect Java SDK


Basic usage:

```java
ClientConfiguration cfg = ClientConfiguration.fromProperties("myconfig.properties");
Client client = Client.create("myclient", cfg);
client.connect();

service = client.getService(<Service>.class);

try {

   // retrielval
   <ResourceObject> obj = service.get("id", null);

   // Creation
   <NewResource> obj = service.create(<NewResource>, null);

} finally {
  client.disconnect();
}
```
Available service classe are in package com/secucard/connect/service   
   
For your own configuration file see: src/main/resources/default-config.properties   