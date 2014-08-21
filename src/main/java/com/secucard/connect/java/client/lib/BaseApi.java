/**
 * BaseApi.java class file
 */
package com.secucard.connect.java.client.lib;

/**
 * Class that should be the parent class of all api calls
 */
public abstract class BaseApi {

	private final ResourceFactory resourceFactory;

	/**
	 * Constructor
	 * @param resourceFactory
	 */
	public BaseApi(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	/**
	 * ResourceFactory getter
	 * @return ResourceFactory
	 */
	protected ResourceFactory getResourceFactory() {
		return resourceFactory;
	}
}