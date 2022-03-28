package io.spring.infrastructure.custom;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * RequestBody LocalDate 요소에 2022-01-26T05:58:03.119Z 꼴의 string이 왔을 때 deserialize 해주는 custom deserilizer
 */
public class CustomLocalDateDeSerializer extends JsonDeserializer<LocalDate> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String t = jsonParser.getText();
        if(t.trim().equals("")){
            return null;
        }
        return LocalDate.parse(jsonParser.getText().split("T")[0], DATE_FORMAT);
    }
}
