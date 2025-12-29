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

import com.zhengshuyun.oktool.core.date.DateFormatConstant;
import com.zhengshuyun.oktool.core.date.ZoneConstant;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Toint
 * @since 2025/12/29
 */
public class JsonConfig {
    private String dateTimeFormat = DateFormatConstant.NORM_DATETIME_PATTERN;
    private String dateFormat = DateFormatConstant.NORM_DATE_PATTERN;
    private String timeFormat = DateFormatConstant.NORM_TIME_PATTERN;
    private ZoneId zone = ZoneConstant.SHANGHAI;
    private Locale locale = Locale.CHINA;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JsonConfig that = (JsonConfig) o;
        return Objects.equals(dateTimeFormat, that.dateTimeFormat) && Objects.equals(dateFormat, that.dateFormat) && Objects.equals(timeFormat, that.timeFormat) && Objects.equals(zone, that.zone) && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTimeFormat, dateFormat, timeFormat, zone, locale);
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public ZoneId getZone() {
        return zone;
    }

    public void setZone(ZoneId zone) {
        this.zone = zone;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
