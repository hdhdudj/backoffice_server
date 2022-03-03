package io.spring.infrastructure.custom;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * RequestBody LocalDateTime 요소에 T가 붙지 않은 형식으로 날짜가 들어왔을 때 deserialize 해주는 custom deserilizer
 */
public class CustomLocalDateTimeDeSerializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

		// if

        return LocalDateTime.parse(jsonParser.getText(), DATE_FORMAT);
    }
}