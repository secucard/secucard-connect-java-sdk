/**
 * BaseFactory.java class file
 */
package com.secucard.connect.java.client.lib;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory to make it simple to get a specific API to work with
 */
public class BaseFactory {

	private final ResourceFactory resourceFactory;

	/**
	 * Constructor
	 * @param resourceFactory
	 */
	public BaseFactory(ResourceFactory resourceFactory) {
		super();
		this.resourceFactory = resourceFactory;
	}

	/**
	 * GetApi function to create api class
	 * @param apiClass
	 * @return
	 */
	public <T extends BaseApi> T getApi(Class<T> apiClass) {
		try {
			return apiClass.getConstructor(ResourceFactory.class).newInstance(
					this.resourceFactory);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}