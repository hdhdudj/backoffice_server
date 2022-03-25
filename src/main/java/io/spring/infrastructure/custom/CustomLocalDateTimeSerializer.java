package io.spring.infrastructure.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/**
 * ResponseBody LocalDateTime 요소에 T가 붙지 않은 형식으로 날짜를 serialize 해주는 custom serilizer
 */
public class CustomLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if(provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS))
        {
            generator.writeStartArray();
            generator.writeNumber(dateTime.getYear());
            generator.writeNumber(dateTime.getMonthValue());
            generator.writeNumber(dateTime.getDayOfMonth());
            generator.writeNumber(dateTime.getHour());
            generator.writeNumber(dateTime.getMinute());
            if(dateTime.getSecond() > 0 || dateTime.getNano() > 0)
            {
                generator.writeNumber(dateTime.getSecond());
                if(dateTime.getNano() > 0)
                {
                    if(provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS))
                        generator.writeNumber(dateTime.getNano());
                    else
                        generator.writeNumber(dateTime.get(ChronoField.MILLI_OF_SECOND));
                }
            }
            generator.writeEndArray();
        }
        else
        {
            String dateString = dateTime.toString();
            String addLocTm = dateTime.getSecond() == 0? ":00" : "";
            dateString = dateString + addLocTm;
            generator.writeString(dateString.replace('T', ' '));
        }
    }
}
