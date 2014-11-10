/**
 * DateTimeSerializer.java class file
 */
package com.secucard.connect.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

public class DateTimeSerializer extends JsonSerializer<DateTime> {

	@Override
	public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeString(DateTimeUtil.formatDateTime(value));
	}

}