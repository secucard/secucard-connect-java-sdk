/**
 * SkeletonsResponse.java class file
 */
package com.secucard.connect.java.client.general;


import com.fasterxml.jackson.annotation.JsonProperty;

public class SkeletonsResponse {

	/**
	 * The id of the created skeleton
	 */
	private int id;

	@JsonProperty("id")
	public int getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(int id) {
		this.id = id;
	}
}
