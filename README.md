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

## Use as Maven Dependency

To use this projects artifacts with Maven add the following dependency and repository to your POM:

``` xml
    <dependency>
      <groupId>com.secucard.connect</groupId>
      <artifactId>secucard-connect-java-sdk</artifactId>
      <version>0.3.2</version>
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