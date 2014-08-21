/**
 * Skeletons.java class file
 */
package com.secucard.connect.java.client.general;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * Represents a Skeletons model
 */
@JsonAutoDetect()
public final class Skeletons {

	private String id;

	private String a;

	private String b;

	private String c;

	/**
	 * Constructor
	 */
	public Skeletons() {
	}

	@Override
	public String toString() {
		return "Bookmark [id=" + id + ", a=" + a + ", b=" + b + ", c=" + c + "]";
	}

	public String getId() {
		return id;
	}

	public String getA() {
		return a;
	}

	public String getB() {
		return b;
	}

	public String getC() {
		return c;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setA(String a) {
		this.a = a;
	}

	public void setB(String b) {
		this.b = b;
	}

	public void setC(String c) {
		this.c = c;
	}
}