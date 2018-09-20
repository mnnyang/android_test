package cn.xxyangyoulin.test_okhttp;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.xxyangyoulin.library.LogUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UseTest {

    public static final String HOST = "http://10.0.2.2:8000";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FILE
            = MediaType.parse("application/octet-stream; charset=utf-8");

    //1. 建立客户端
    //private OkHttpClient client = new OkHttpClient();
    private OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(new LoggingInterceptor())
            .build();

    public UseTest() {
        //get();
        post();
    }


    private void post() {
        //传递json
        //RequestBody body = RequestBody.create(JSON, "{'dd':'test.txt'}");

        //传递参数
        FormBody body = new FormBody.Builder()
                .add("param", "parameter test")
                .build();

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //上传文件
        File file = new File(absolutePath+"/Download","demo.jpg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        "demo.jpg",
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();


        final Request request = new Request.Builder()
                //.url(HOST + "/test/params/")
                .url(HOST + "/test/upload_file/")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    LogUtil.e("body", response.body().string());
                    for (int i = 0; i < response.headers().size(); i++) {
                        LogUtil.e("headers", response.headers().name(i) + "---" +
                        response.headers().value(i));
                    }

                } else {
                    LogUtil.e(this, "failed");
                }
            }
        });
    }

    public void get() {
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                //.addHeader()
                .get()
                .build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //这里是子线程
                LogUtil.e("当前线程", Thread.currentThread().getName());
                //InputStream inputStream = response.body().byteStream();
                LogUtil.e(this, response.body().string());
            }
        });
    }
}
