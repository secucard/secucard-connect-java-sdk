/**
 * DateTimeDeserializer.java class file
 */
package com.secucard.connect.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return DateTimeUtil.parseDateTime(jp.getText());
	}
}