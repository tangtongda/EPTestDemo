package com.temperature.commons.utils;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * OkHttp Retry interceptor implements
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public class RetryInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryInterceptor.class);

    /**
     * max retry count
     */
    private final int retryTimes;

    /**
     * retry interval duration
     */
    private final long retryInterval;

    RetryInterceptor(Builder builder) {
        this.retryTimes = builder.retryTimes;
        this.retryInterval = builder.retryInterval;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        int retryNum = 0;
        LOGGER.info("retry count:{}", retryNum);
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < retryTimes) {
            retryNum++;
            LOGGER.warn("request is not successful, retrying...:{} times", retryNum);
            response = chain.proceed(request);
        }
        return response;
    }

    /**
     * retry duration
     */
    public long getRetryInterval() {
        return this.retryInterval;
    }


    public static final class Builder {
        private int retryTimes;
        private long retryInterval;

        public Builder() {
            retryTimes = 3;
            retryInterval = 1000;
        }

        public RetryInterceptor.Builder setRetryTimes(int executionCount) {
            this.retryTimes = executionCount;
            return this;
        }

        public RetryInterceptor.Builder setRetryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        public RetryInterceptor build() {
            return new RetryInterceptor(this);
        }
    }


}
