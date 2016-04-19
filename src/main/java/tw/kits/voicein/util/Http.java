package tw.kits.voicein.util;

import java.io.IOException;
import java.util.HashMap;
import okhttp3.Headers;
import okhttp3.Headers.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Calvin
 */
public class Http {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    public Response postResponse(String url, String json, Headers headers) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }
    public Response postResponse(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apiKey", Parameter.API_KEY)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public String post(String url, String json) throws IOException {

        return postResponse(url, json).body().string();
    }

}
