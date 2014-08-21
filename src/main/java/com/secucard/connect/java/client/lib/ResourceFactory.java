/**
 * ResourceFactory.java class file
 */
package com.secucard.connect.java.client.lib;

import java.net.URI;
import java.net.URISyntaxException;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.eclipse.jetty.http.HttpHeaders;
import org.joda.time.DateTime;

import com.secucard.connect.java.client.oauth.OAuthClientCredentials;
import com.secucard.connect.java.client.oauth.OAuthUserCredentials;
import com.secucard.connect.java.client.serialize.DateTimeDeserializer;
import com.secucard.connect.java.client.serialize.DateTimeSerializer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.multipart.impl.MultiPartWriter;

/**
 * This is the main low level entry point to access the Secucard API.
 * Construct this and pass it to the APIFactory
 */
public final class ResourceFactory {

	private final WebResource apiResource;
	private final WebResource fileResource;

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
		ClientConfig config = new DefaultClientConfig();
		config.getSingletons().add(getJsonProvider());
		config.getClasses().add(MultiPartWriter.class);
		Client client = Client.create(config);
		client.addFilter(new GZIPContentEncodingFilter(false));
		client.addFilter(new ExceptionFilter());

		// uncomment following line to see the HTTP requests
		client.addFilter(new LoggingFilter(System.out));

		this.apiResource = client.resource(getURI(apiHostname, port, ssl));
		apiResource.header(HttpHeaders.USER_AGENT, "Secucore Java API Client");
		this.fileResource = client.resource(getURI(fileHostname, port, ssl));
		fileResource.header(HttpHeaders.USER_AGENT, "Secucore Java API Client");

		AuthProvider authProvider = new AuthProvider(this, clientCredentials, userCredentials);
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
		mapper.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
		mapper.setSerializationInclusion(Inclusion.NON_NULL);

		SimpleModule secucoreModule = new SimpleModule("Secucore", new Version(1, 0, 0, null))
			.addSerializer(DateTime.class, new DateTimeSerializer())
			
			.addDeserializer(DateTime.class, new DateTimeDeserializer());

		mapper.registerModule(secucoreModule);

		return new CustomJacksonJsonProvider(mapper);
	}

	public WebResource getFileResource(String path) {
		return getFileResource(path, true);
	}

	public WebResource getFileResource(String path, boolean secure) {
		WebResource subResource = fileResource.path(path);
		if (secure) {
			subResource.addFilter(this.loginFilter);
		}

		return subResource;
	}

	public WebResource getApiResource(String path) {
		return getApiResource(path, true);
	}

	public WebResource getApiResource(String path, boolean secure) {
		WebResource subResource = apiResource.path(path);
		if (secure) {
			subResource.addFilter(this.loginFilter);
		}

		return subResource;
	}
}
