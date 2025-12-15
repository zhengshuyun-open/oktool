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

package com.zhengshuyun.oktool.spring.boot.xda.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * 玺得安客户端配置
 *
 * @author Toint
 * @since 2025/12/16
 */
public class XdaClientConfig {
    /**
     * 玺得安服务地址. 示例: {@code https://zhengshuyun.com}
     */
    @NotBlank
    private String host;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String tokenCacheKey = "xda:token";

    /**
     * Token安全预留时间, 单位秒
     * 玺得安token重复获取的值是一样的, 所以存在临界值, 在请求过程中可能过期
     * 如果小于安全时间, 就重复去服务器拉去新的token, 直到token被刷新
     * 注意: 不要设置的时间过长, 因为会重复去服务器拉取新的token, 线程会阻塞
     */
    @NotNull
    @Min(0)
    @Max(60)
    private int tokenRefreshThreshold = 2;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        XdaClientConfig that = (XdaClientConfig) o;
        return tokenRefreshThreshold == that.tokenRefreshThreshold && Objects.equals(host, that.host) && Objects.equals(clientId, that.clientId) && Objects.equals(clientSecret, that.clientSecret) && Objects.equals(tokenCacheKey, that.tokenCacheKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, clientId, clientSecret, tokenCacheKey, tokenRefreshThreshold);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenCacheKey() {
        return tokenCacheKey;
    }

    public void setTokenCacheKey(String tokenCacheKey) {
        this.tokenCacheKey = tokenCacheKey;
    }

    public int getTokenRefreshThreshold() {
        return tokenRefreshThreshold;
    }

    public void setTokenRefreshThreshold(int tokenRefreshThreshold) {
        this.tokenRefreshThreshold = tokenRefreshThreshold;
    }
}
