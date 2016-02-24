package tw.kits.voicein.util;
import java.io.IOException;
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
  private static final String KITS_API_KEY = "2a4e0dd8db3807790d853dabf0f448de21cea6057b5dc48539330f934e9bddfb";
  
  OkHttpClient client = new OkHttpClient();

  public String post(String url, String json) throws IOException {
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .addHeader("apiKey", KITS_API_KEY)
        .post(body)
        .build();
    Response response = client.newCall(request).execute();
    return response.body().string();
  }
    
}
