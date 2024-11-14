package org.system.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class ElasticsearchConverterConfig {

    @Bean
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(
                new DateToLocalDateTimeConverter(),
                new LocalDateTimeToDateConverter()
        ));
    }

    @ReadingConverter
    private static class DateToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            if (source == null) {
                return null;
            }

            // Try different date formats
            try {
                if (source.length() == 10) {  // If it's just a date like "2024-11-01"
                    return LocalDateTime.parse(source + "T00:00:00");
                }
                return LocalDateTime.parse(source, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                try {
                    return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception e2) {
                    throw new IllegalArgumentException("Unable to parse date: " + source, e2);
                }
            }
        }
    }

    @WritingConverter
    private static class LocalDateTimeToDateConverter implements Converter<LocalDateTime, String> {
        @Override
        public String convert(LocalDateTime source) {
            return source == null ? null : source.format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }
}