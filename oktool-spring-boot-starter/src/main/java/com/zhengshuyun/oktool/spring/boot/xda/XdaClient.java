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

package com.zhengshuyun.oktool.spring.boot.xda;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.thread.ThreadUtil;
import cn.hutool.v7.http.client.Request;
import cn.hutool.v7.http.client.Response;
import cn.hutool.v7.http.client.body.HttpBody;
import cn.hutool.v7.http.client.body.MultipartBody;
import cn.hutool.v7.http.meta.Method;
import com.fasterxml.jackson.databind.JsonNode;
import com.zhengshuyun.oktool.core.util.Assert;
import com.zhengshuyun.oktool.core.util.JacksonUtil;
import com.zhengshuyun.oktool.spring.boot.cache.Cache;
import com.zhengshuyun.oktool.spring.boot.xda.model.XdaClientConfig;
import com.zhengshuyun.oktool.spring.boot.xda.model.XdaResponse;
import com.zhengshuyun.oktool.spring.boot.xda.model.XdaToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 玺得安印章客户端
 *
 * @author Toint
 * @see <a href="https://open.yda.cn/document/openPlatform/apiDescription">玺得安开放平台</a>
 * @since 2025/12/16
 */
public class XdaClient {

    private final XdaClientConfig config;
    private final Cache cache;

    public XdaClient(XdaClientConfig config, Cache cache) {
        Assert.validate(config);
        Assert.notNullParam(cache, "cache");
        this.config = config;
        this.cache = cache;
    }

    /**
     * 删除用户
     *
     * @param staffId 用户ID
     * @throws RuntimeException 删除失败
     */
    public void deleteUser(Long staffId) {
        Assert.notNullParam(staffId, "staffId");

        String url = config.host()
                .addPath("/api/user/" + staffId)
                .build();

        try (Response response = Request.of(url)
                .method(Method.DELETE)
                .send()) {

            String resBody = response.bodyStr();
            int status = response.getStatus();
            Assert.isTrue(response.isOk(),
                    "玺得安删除用户失败, HTTP状态: {}, 响应: {}",
                    status,
                    resBody);
            Assert.notBlank(resBody, "玺得安删除用户失败, 响应为空");

            XdaResponse xdaResponse = JacksonUtil.tryReadValue(resBody, XdaResponse.class);
            Assert.notNull(xdaResponse, "玺得安删除用户失败, 响应数据格式非法, 响应: {}", resBody);

            boolean data = Optional.ofNullable(xdaResponse.getData())
                    .map(JsonNode::asBoolean)
                    .orElse(false);

            Assert.isTrue(xdaResponse.isOk() && data, "玺得安删除用户失败: {}", xdaResponse.getMessage());

        } catch (IOException e) {
            throw new RuntimeException("玺得安删除用户失败, 网络异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取令牌, 内置缓存
     *
     * @return 令牌
     */
    public String getToken() {
        String tokenCacheKey = config.getTokenCacheKey();
        String tokenCacheValue = cache.get(tokenCacheKey);
        if (StringUtils.isNotBlank(tokenCacheValue)) {
            return tokenCacheValue;
        }

        int tokenRefreshThreshold = config.getTokenRefreshThreshold();
        for (int i = 0; i < 10; i++) {
            XdaToken xdaToken = fetchToken();
            String accessToken = xdaToken.getAccessToken();
            Integer expiresIn = xdaToken.getExpiresIn();

            /*
             * 玺得安token重复获取的值是一样的, 所以存在临界值
             * 如果小于安全时间, 就重复去服务器拉去新的token, 直到token被刷新*/
            if (expiresIn <= tokenRefreshThreshold) {
                ThreadUtil.sleep(1000);
            } else {
                cache.put(tokenCacheKey, accessToken, expiresIn - tokenRefreshThreshold);
                return accessToken;
            }
        }

        throw new RuntimeException("玺得安获取令牌失败, 有效期始终小于等于阈值: " + tokenRefreshThreshold);
    }

    /**
     * 从玺得安服务器获取令牌
     *
     * @return 经过校验的合法令牌
     */
    private XdaToken fetchToken() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("client_id", config.getClientId());
        requestBody.put("client_secret", config.getClientSecret());
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("scope", "scope1");
        MultipartBody multipartBody = MultipartBody.of(requestBody, StandardCharsets.UTF_8);

        // 返回示例: {"access_token":"7a1abce5-49f1-4e6e-9181-59cab621b816","token_type":"bearer","expires_in":7199,"scope":"scope1"}
        XdaToken xdaToken = request(Method.POST, "/oauth/token", multipartBody, null, XdaToken.class);

        String accessToken = xdaToken.getAccessToken();
        Assert.notBlank(accessToken, "获取玺得安令牌失败, access_token为空");

        Integer expiresIn = xdaToken.getExpiresIn();
        Assert.notNull(expiresIn, "获取玺得安令牌失败, expires_in为空");
        Assert.isTrue(expiresIn >= 0, "获取玺得安令牌失败, expires_in为负数");

        return xdaToken;
    }

    /**
     * 请求方法. 内置校验业务码/携带令牌
     *
     * @return data
     */
    private <T> T requestWithToken(Method method, String url, HttpBody body, Class<T> dataType) {
        XdaResponse xdaResponse = request(
                method,
                url,
                body,
                Map.of(HttpHeaders.AUTHORIZATION, "Bearer " + getToken()),
                XdaResponse.class
        );

        Integer code = xdaResponse.getCode();
        String message = Optional.ofNullable(xdaResponse.getMessage()).orElse("未知错误");
        JsonNode data = xdaResponse.getData();

        Assert.equals(code, 0, "玺得安业务码异常: {}", message);
        return JacksonUtil.treeToValue(data, dataType);
    }

    /**
     * 请求方法.
     * 内置校验HTTP状态码/响应格式
     *
     * @return JSON数据对象
     */
    private <T> T request(Method method, String url, HttpBody body, Map<String, String> headers, Class<T> responseType) {
        String host = StrUtil.removeSuffix(config.getHost(), "/");
        url = StrUtil.prependIfMissing(host, "/");

        try (Response response = Request.of(host + url)
                .method(method)
                .body(body)
                .header(headers)
                .send()) {

            String resBody = response.bodyStr();
            Assert.isTrue(response.isOk(), "玺得安响应HTTP状态码异常: {}", response.getStatus());
            Assert.notBlank(resBody, "玺得安响应为空");

            T jsonResponse = JacksonUtil.tryReadValue(resBody, responseType);
            Assert.notNull(jsonResponse, "玺得安响应数据非JSON格式: {}", resBody);
            return jsonResponse;

        } catch (IOException e) {
            throw new RuntimeException("玺得安通讯异常: " + e.getMessage(), e);
        }
    }
}
