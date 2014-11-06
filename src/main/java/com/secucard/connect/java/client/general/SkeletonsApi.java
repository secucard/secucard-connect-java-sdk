/**
 * SkeletonsApi.java class file
 */
package com.secucard.connect.java.client.general;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import com.secucard.connect.java.client.lib.BaseApi;
import com.secucard.connect.java.client.lib.ResourceFactory;

/**
 * This is the testing api that offers methods to work with Skeletons
 */
public class SkeletonsApi extends BaseApi {

	public SkeletonsApi(ResourceFactory resourceFactory) {
		super(resourceFactory);
	}

	/**
	 * Function to get Skeletons by Id
	 * @param scrollId
	 * @return Skeletons
	 */
	public Skeletons getSkeletonsById(int scrollId) {
		return getResourceFactory().getApiResource("/api/v2/General/Skeletons?scroll_id=" + scrollId).request().get()
        .readEntity(Skeletons.class);
	}

	/**
	 * Returns the list of Skeletons
	 * 
	 * @param count
	 * @param offset
	 * @return The SkeletonsListModel
	 */
	public SkelentonsListModel getSkeletons(int count, int offset) {
		return getResourceFactory().getApiResource(
				"/api/v2/General/Skeletons")
				.queryParam("count", Integer.toString(count))
				.queryParam("offset", Integer.toString(offset)).request()
				.get().readEntity(new GenericType<SkelentonsListModel>() { });
	}

	/**
	 * Creates Skeletons (not working)
	 * 
	 * @param Skeletons
	 * @return The id of new skeletons
	 */
	public int addSkeletons(Skeletons skeleton) {
		return getResourceFactory().getApiResource("/api/v2/General/Skeletons")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(skeleton)).readEntity(SkeletonsResponse.class).getId();
	}

	/**
	 * Deletes the Skeletons (not working)
	 * 
	 * @param scrollId
	 */
	public void deleteSkeleton(int scrollId) {
		getResourceFactory().getApiResource("/api/v2/General/Skeletons/" + scrollId).request().delete();
	}
}
