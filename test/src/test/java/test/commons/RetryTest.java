package test.commons;

import com.temperature.commons.utils.RetryInterceptor;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * ok http custom retry mechanism test
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public class RetryTest {

    String mUrl = "https://http://www.weather.com.cn/";
    OkHttpClient mClient;

    @Before
    public void setUp() {
        mClient = new OkHttpClient.Builder()
                .addInterceptor(new RetryInterceptor.Builder()
                        .setRetryInterval(1000)
                        .setRetryTimes(3)
                        .build()) // retry
                .addInterceptor(new MockRequest())// mock request
                .build();
    }

    @Test
    public void testRequest() throws IOException {
        Request request = new Request.Builder()
                .url(mUrl)
                .build();
        Response response = mClient.newCall(request).execute();
        Assert.assertNotNull(response.body());
    }


    /**
     * test interceptor
     */
    static class MockRequest implements Interceptor {

        @Override
        @NonNull
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String url = request.url().toString();
            System.out.println("url=" + url);
            String responseString = "{\"message\":\"mock data\"}";//mock response data
            return new Response.Builder()
                    .code(500)
                    .request(request)
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                    .addHeader("content-type", "application/json")
                    .message("test")
                    .build();
        }
    }

}