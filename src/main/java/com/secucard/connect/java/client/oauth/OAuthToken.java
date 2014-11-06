/**
 * OAuthToken.java class file
 */
package com.secucard.connect.java.client.oauth;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OAuthToken model class
 */
public final class OAuthToken {

	private String accessToken;

	private String refreshToken;
	
	private String tokenType;
	
	private String scope;

	private int expiresIn;

	@JsonProperty("access_token")
	public String getAccessToken() {
		return accessToken;
	}

	@JsonProperty("access_token")
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@JsonProperty("refresh_token")
	public String getRefreshToken() {
		return refreshToken;
	}

	@JsonProperty("refresh_token")
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@JsonProperty("expires_in")
	public int getExpiresIn() {
		return expiresIn;
	}

	@JsonProperty("expires_in")
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	@JsonProperty("token_type")
	public String getTokenType() {
		return tokenType;
	}

	@JsonProperty("token_type")
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@JsonProperty("scope")
	public String getScope() {
		return scope;
	}

	@JsonProperty("scope")
	public void setScope(String scope) {
		this.scope = scope;
	}
}