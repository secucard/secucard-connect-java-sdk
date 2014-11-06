/**
 * SkelentonsListModel.java class file
 */
package com.secucard.connect.java.client.general;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Array;



/**
 * Represents a SkelentonsListModel model
 */
@JsonAutoDetect()
public final class SkelentonsListModel {

	private String scrollId;

	private Integer count;
	
	private Integer offset;

	// todo think how we can pass various objects here
	//private Array data;

	public SkelentonsListModel(String scrollId, Integer count, Integer offset, Array data) {
		super();
		this.scrollId = scrollId;
		this.count = count;
		this.offset = offset;
		//this.data = data;
	}

	public SkelentonsListModel(String scrollId, Integer count, Integer offset) {
		super();
		this.scrollId = scrollId;
		this.count = count;
		this.offset = offset;
	}
	
	public SkelentonsListModel() {
	}

	@JsonProperty("scroll_id")
	public String getScrollId() {
		return scrollId;
	}
	
	@JsonProperty("scroll_id")
	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

	@JsonProperty("count")
	public Integer getCount() {
		return count;
	}

	@JsonProperty("count")
	public void setCount(Integer count) {
		this.count = count;
	}

	@JsonProperty("offset")
	public Integer getOffset() {
		return offset;
	}

	@JsonProperty("offset")
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
/*
	@JsonProperty("data")
	public Array getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(Array data) {
		this.data = data;
	}*/

	@Override
	public String toString() {
		return "SkelentonsListModel [scrollId=" + scrollId + ", count=" + count + ", offset=" + offset + ", data=" + /*data.toString() +*/ "]";
	}
}