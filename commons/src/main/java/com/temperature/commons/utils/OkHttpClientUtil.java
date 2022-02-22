package com.temperature.commons.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * {@link OkHttpClientUtil}
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public class OkHttpClientUtil {

    public static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    public static final MediaType FORM_DATA_MEDIA_TYPE = MediaType.parse("application/from-data");

    private OkHttpClientUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpClientUtil.class);

    /**
     * default max retry count
     */
    private static final int MAX_RETRY_TIMES = 5;
    /**
     * default max retry interval
     */
    private static final int MAX_RETRY_INTERVAL = 1000;

    private static final long OKHTTP_TIME_OUT = 6000;

    /**
     * get a url
     *
     * @param url request url
     * @return response data
     */
    public static String get(@NotEmpty String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                // open okhttp retry mechanism
                .addInterceptor(new RetryInterceptor.Builder()
                        .setRetryInterval(MAX_RETRY_INTERVAL)
                        .setRetryTimes(MAX_RETRY_TIMES)
                        .build())// add retry interceptor
                .connectTimeout(OKHTTP_TIME_OUT, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (null == response.body()) {
                return null;
            }
            return response.body().string();
        } catch (IOException e) {
            LOGGER.error("okhttp get request error,url:{}", url, e);
        }
        return null;
    }

}
