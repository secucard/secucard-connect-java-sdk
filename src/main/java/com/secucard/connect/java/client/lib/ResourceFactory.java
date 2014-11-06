/**
 * ResourceFactory.java class file
 */
package com.secucard.connect.java.client.lib;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.auth.AuthProviderImpl;
import org.eclipse.jetty.util.MultiPartWriter;
import org.glassfish.jersey.client.ClientConfig;
import org.joda.time.DateTime;

import com.secucard.connect.java.client.oauth.OAuthClientCredentials;
import com.secucard.connect.java.client.oauth.OAuthUserCredentials;
import com.secucard.connect.java.client.serialize.DateTimeDeserializer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * This is the main low level entry point to access the Secucard API.
 * Construct this and pass it to the APIFactory
 */
public final class ResourceFactory {

	private final WebTarget apiResource;
	private final WebTarget fileResource = null;

	private final LoginFilter loginFilter;

	/**
	 * Constructor
	 * @param clientCredentials
	 * @param userCredentials
	 */
	public ResourceFactory(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials) {
		this("connect.secucard.com", "file.secucard.com", 443, true, false, clientCredentials, userCredentials);
	}

	/**
	 * Constructor
	 * @param apiHostname
	 * @param fileHostname
	 * @param port
	 * @param ssl
	 * @param dryRun
	 * @param clientCredentials
	 * @param userCredentials
	 */
	public ResourceFactory(String apiHostname, String fileHostname, int port, boolean ssl, boolean debug,
			OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials) {
		ClientConfig config = new ClientConfig();
		config.register(getJsonProvider());
		config.getClasses().add(MultiPartWriter.class);
		Client client = ClientBuilder.newClient();
//		client.register(new GZIPContentEncodingFilter(false));
		client.register(new ExceptionFilter());

		// uncomment following line to see the HTTP requests
//		client.register(new LoggingFilter(System.out));

		this.apiResource = client.target(getURI(apiHostname, port, ssl));
//		apiResource.(HttpHeaders.USER_AGENT, "Secucore Java API Client");
//		this.fileResource = client.resource(getURI(fileHostname, port, ssl));
//		fileResource.t(HttpHeaders.USER_AGENT, "Secucore Java API Client");

		AuthProviderImpl authProvider = new AuthProviderImpl(this, clientCredentials, userCredentials);
		this.loginFilter = new LoginFilter(authProvider);
	}

	/**
	 * Function to get URI
	 * @param hostname
	 * @param port
	 * @param ssl
	 * @return
	 */
	private URI getURI(String hostname, int port, boolean ssl) {
		try {
			return new URI(ssl ? "https" : "http", null, hostname, port, null,
					null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * JsonProvider creator
	 * @return
	 */
	private JacksonJsonProvider getJsonProvider() {
		ObjectMapper mapper = new ObjectMapper();
//		mapper.disable(JsonFactory.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
//		mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
//		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

		SimpleModule secucoreModule = new SimpleModule("Secucore", new Version(1, 0, 0, null))
//			.addSerializer(DateTime.class, new DateTimeSerializer())
			
			.addDeserializer(DateTime.class, new DateTimeDeserializer());

		mapper.registerModule(secucoreModule);

		return null; //ew CustomJacksonJsonProvider(null);
	}

	public WebTarget getFileResource(String path) {
		return getFileResource(path, true);
	}

	public WebTarget getFileResource(String path, boolean secure) {
    WebTarget subResource = fileResource.path(path);
		if (secure) {
			subResource.register(this.loginFilter);
		}

		return subResource;
	}

	public WebTarget getApiResource(String path) {
		return getApiResource(path, true);
	}

	public WebTarget getApiResource(String path, boolean secure) {
    WebTarget subResource = apiResource.path(path);
		if (secure) {
			subResource.register(this.loginFilter);
		}

		return subResource;
	}
}
