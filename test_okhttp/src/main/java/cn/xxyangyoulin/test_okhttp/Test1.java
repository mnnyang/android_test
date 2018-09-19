package cn.xxyangyoulin.test_okhttp;

import android.app.Activity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test1 {

    private OkHttpClient client = new OkHttpClient
            .Builder()
            .cache(new Cache(new File("cache"), 20 * 1024 * 1024))
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    public void exe(Activity activity) {

        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .get()
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });

        /*同步*/
        /*try {
            Response execute = call.execute();
            System.out.println(execute.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
