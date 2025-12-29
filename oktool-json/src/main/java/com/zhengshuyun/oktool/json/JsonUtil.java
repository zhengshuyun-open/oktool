/*
 * Copyright 2025 Toint (599818663@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhengshuyun.oktool.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Toint
 * @since 2025/12/29
 */
public class JsonUtil {

    private static final ObjectMapper INSTANCE = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        return createObjectMapper(new JsonConfig());
    }

    public static ObjectMapper createObjectMapper(JsonConfig jsonConfig) {
        Preconditions.checkNotNull(jsonConfig, "JsonConfig must not be null");
        ObjectMapper mapper = new ObjectMapper();

        // 基础配置
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // Java 8 时间
        configJavaTimeModule(mapper, jsonConfig);

        // java.util.Date
        configDateFormat(mapper, jsonConfig);

        // Long 转 String
        configLongToString(mapper);

        return mapper;
    }

    /**
     * 配置Java8时间模块
     */
    private static void configJavaTimeModule(ObjectMapper mapper, JsonConfig jsonConfig) {
        JavaTimeModule module = new JavaTimeModule();

        Locale locale = jsonConfig.getLocale();
        Preconditions.checkNotNull(locale, "Locale must not be null");

        ZoneId zone = jsonConfig.getZone();
        Preconditions.checkNotNull(zone, "ZoneId must not be null");

        // LocalDateTime
        String dateTimeFormat = Strings.nullToEmpty(jsonConfig.getDateTimeFormat());
        Preconditions.checkArgument(!dateTimeFormat.isBlank(), "dateTimeFormat must not be blank");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat, locale).withZone(zone);
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        // LocalDate
        String dateFormat = Strings.nullToEmpty(jsonConfig.getDateFormat());
        Preconditions.checkArgument(!dateFormat.isBlank(), "dateFormat must not be blank");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(dateFormat, locale).withZone(zone);
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        // LocalTime
        String timeFormat = Strings.nullToEmpty(jsonConfig.getTimeFormat());
        Preconditions.checkArgument(!timeFormat.isBlank(), "timeFormat must not be blank");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(timeFormat, locale).withZone(zone);
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        mapper.registerModule(module);
    }

    /**
     * 配置 Date 格式
     */
    private static void configDateFormat(ObjectMapper mapper, JsonConfig config) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(config.getDateFormat());
        dateFormat.setTimeZone(TimeZone.getTimeZone(config.getZone()));
        mapper.setDateFormat(dateFormat);
        mapper.setTimeZone(TimeZone.getTimeZone(config.getZone()));
    }

    /**
     * 配置 Long 转 String
     */
    private static void configLongToString(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(java.math.BigInteger.class, ToStringSerializer.instance);
        mapper.registerModule(module);
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

}
