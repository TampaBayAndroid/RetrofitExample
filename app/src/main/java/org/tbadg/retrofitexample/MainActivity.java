package org.tbadg.retrofitexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings({"NonConstantStringShouldBeStringBuffer",
                   "StringConcatenationInLoop",
                   "ConstantConditions", "unused"})
public class MainActivity extends AppCompatActivity {

    // Used only for the call that uses caching:
    private TextView tags;
    private static ConnectivityManager connMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tags = findViewById(R.id.tags);
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void getTagsByPopularity(final View view) {
        tags.setText("");

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/2.2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        TagsApi tagsApi = retrofit.create(TagsApi.class);

        Call<Tags> TagsRequestCall = tagsApi.getTagsByPopularity();
        TagsRequestCall.enqueue(new Callback<Tags>() {
            @Override
            public void onResponse(@NonNull Call<Tags> call,
                                   @NonNull Response<Tags> response) {
                if (response.isSuccessful()) {
                    String output = "";
                    for (Item item : response.body().getItems())
                        output += item.getName() + ": " + item.getCount() + '\n';

                    tags.setText(output);

                } else {
                    Toast.makeText(MainActivity.this, response.message(),
                                   Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tags> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTagsByName(final View view) {
        tags.setText("");

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/2.2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        TagsApi tagsApi = retrofit.create(TagsApi.class);

        Call<Tags> TagsRequestCall = tagsApi.getTagsByName("desc");
        TagsRequestCall.enqueue(new Callback<Tags>() {
            @Override
            public void onResponse(@NonNull Call<Tags> call,
                                   @NonNull Response<Tags> response) {
                if (response.isSuccessful()) {
                    String output = "";
                    for (Item item : response.body().getItems())
                        output += item.getName() + ": " + item.getCount() + '\n';

                    tags.setText(output);

                } else {
                    Toast.makeText(MainActivity.this, response.message(),
                                   Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tags> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getTagsByCache(final View view) {
        tags.setText("");

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .cache(new Cache(new File(getCacheDir(), "http-cache"), 50 * 1024 * 1024))
            .addNetworkInterceptor(responseCacheInterceptor)
            .addInterceptor(offlineResponseCacheInterceptor)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com/2.2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        TagsApi tagsApi = retrofit.create(TagsApi.class);

        Call<Tags> TagsRequestCall = tagsApi.getTagsByPopularity();
        TagsRequestCall.enqueue(new Callback<Tags>() {
            @Override
            public void onResponse(@NonNull Call<Tags> call,
                                   @NonNull Response<Tags> response) {
                if (response.isSuccessful()) {
                    Log.e("", "onResponse: ");
                    String output = "";
                    for (Item item : response.body().getItems())
                        output += item.getName() + ": " + item.getCount() + '\n';

                    tags.setText(output);

                } else {
                    Log.e("", "onResponse: " + response.toString());
                    Toast.makeText(MainActivity.this, response.message(),
                                   Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tags> call, @NonNull Throwable t) {
                Log.e("", "onFailure: " + t.toString());
                Toast.makeText(MainActivity.this, t.getMessage(),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final Interceptor responseCacheInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                                   .removeHeader("Pragma")
                                   // Use a really short max-age in order to see caching at work:
                                   .header("Cache-Control", "public, max-age=" + 10 /* secs */)
                                   .build();
        }
    };

    private final Interceptor offlineResponseCacheInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
            Request request = chain.request();
            if (netInfo == null || !netInfo.isConnected()) {
                request = request.newBuilder()
                                 .removeHeader("Pragma")
                                 .header("Cache-Control", "public, only-if-cached")
                                 .build();
            }
            return chain.proceed(request);
        }
    };
}
