package io.spring.infrastructure.custom;

import org.springframework.boot.autoconfigure.web.format.WebConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;

/**
 * ReqeustBody LocalDate 요소에 ""가 오는 경우 null 리턴하는 컨버터
 */
@Component
class StringToLocalDateConverter
        implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        if(source.trim().equals("")){
            return null;
        }
        //2022-01-26T05:58:03.119Z
        String source2 = source;
        if(source.split("T").length > 1){
            source2 = source.split("T")[0];
        }
        return LocalDate.parse(
                source2, DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
