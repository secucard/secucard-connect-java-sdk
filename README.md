# secucard connect Java SDK

**DEPRECATED**: If you start with a new project please use this SDK: https://github.com/secuconnect/secuconnect-java-sdk

## Basic usage:

```java
SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();  //use default config

// or

SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get(<path>);  //use your config from path

ClientAuthDetails authDetails = new AbstractClientAuthDetails(<your-store-dir>) {
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
```

Available service classe are in package com/secucard/connect/product
   
For your own configuration file see: src/main/resources/config.properties

## Use as Maven Dependency

To use this projects artifacts with Maven add the following dependency and repository to your POM:

``` xml
    <dependency>
      <groupId>com.secucard.connect</groupId>
      <artifactId>secucard-connect-java-sdk</artifactId>
      <version>2.13.4</version>
    </dependency>
    .
    .
    .
    <repository>
        <id>github-secucard</id>
        <url>https://raw.githubusercontent.com/secucard/secucard-connect-java-sdk/mvn-repo</url>
    </repository>
```

## For Developers: Publish as Maven Repository
To allow the usage of this projects build artifacts (secucard-connect-java-sdk-xxx.jar and optional sources) in projects via Maven the artifacts can be pushed in apropriate form to the "mvn-repo" branch of this project.
The simplest approach is: checkout the "mvn-repo" branch, build the artifacts by using mvn package/install, copy the artifacts to the local git repo using the mvn install command below and then push the changes back.

```
mvn install:install-file
    -Dfile=<path to secucard-connect-java-sdk jar>
    -Dsources=<path to sources jar>
    -DpomFile=<path to jar's pom.xml>
    -DlocalRepositoryPath=<path to the local git repository where the "mvn-repo" branch lives>
    -DcreateChecksum=true
    -DupdateReleaseInfo=true
```
