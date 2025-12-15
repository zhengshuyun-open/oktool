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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Toint
 * @since 2025/12/16
 */
public class XdaToken {
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 有效时间, 单位秒
     */
    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("scope")
    private String scope;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        XdaToken xdaToken = (XdaToken) o;
        return Objects.equals(accessToken, xdaToken.accessToken) && Objects.equals(expiresIn, xdaToken.expiresIn) && Objects.equals(tokenType, xdaToken.tokenType) && Objects.equals(scope, xdaToken.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, expiresIn, tokenType, scope);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
