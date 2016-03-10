# secucard connect Java SDK

Basic usage:

    SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();  //use default config
    
    // or 
    
    SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get(<path>);  //use your config form path
    
    AbstractClientAuthDetails authDetails = new AbstractClientAuthDetails(<your-store-dir>) {
      @Override
      public OAuthCredentials getCredentials() {
        return new ClientCredentials(
              "your-client-id", "your-client-secret");
      }
    
      @Override
      public ClientCredentials getClientCredentials() {
        return (ClientCredentials) this.getCredentials();
      }
    };
    
    cfg.clientAuthDetails = authDetails;
    
    
    // Get a API client instance.
    SecucardConnect client = SecucardConnect.create(cfg);
    
    client.open();
    
    service = client.service(<Service>.class);
    // or
    service = client.<product>.<resource> // like client.payment.secupaydebits   
         
    try {
    
       // retrielval
       <ResourceObject> obj = service.get("id", null);
       
       // Creation
       <NewResource> obj = service.create(<NewResource>, null);
    
    } finally {
      client.close()
    }

    
Available service classe are in package com/secucard/connect/product
   
For your own configuration file see: src/main/resources/config.properties