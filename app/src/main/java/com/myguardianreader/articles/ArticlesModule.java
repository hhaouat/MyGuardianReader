package com.myguardianreader.articles;

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.myguardianreader.BuildConfig;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.myguardianreader.articles.model.ArticleApiMapper;
import com.myguardianreader.articles.model.ArticleMapper;
import com.myguardianreader.repository.GuardianRepository;
import com.myguardianreader.repository.remote.GuardianService;
import com.myguardianreader.repository.remote.GuardianClient;
import com.myguardianreader.details.DetailsPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesModule {
    private static final String BASE_URL = "http://content.guardianapis.com";
    private static final String HEADER_API_KEY = "api-key";

    ArticlesPresenter inject(Context context) {
        return new ArticlesPresenter(AndroidSchedulers.mainThread(),
                new GuardianRepository(
                        new GuardianService(
                                createGuardianService(context),
                                new ArticleMapper(), new ArticleApiMapper()), Schedulers.io(), new DbFavorites(context)));
    }

    public DetailsPresenter injectDetails(Context context) {
        return new DetailsPresenter(AndroidSchedulers.mainThread(), Schedulers.io(),
                new GuardianService(createGuardianService(context), new ArticleMapper(), new ArticleApiMapper()));
    }

    private GuardianClient createGuardianService(Context context) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(createOkHttpClient(context.getResources()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(GuardianClient.class);
    }

    private OkHttpClient createOkHttpClient(Resources resources) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(getAuthInterceptor(resources));
        clientBuilder.addInterceptor(loggingInterceptor);
        return clientBuilder.build();
    }

    private Interceptor getAuthInterceptor(Resources resources) {
        return chain -> {
            Request original = chain.request();
            Headers.Builder hb = original.headers().newBuilder();
            hb.add(HEADER_API_KEY, BuildConfig.API_KEY);
            return chain.proceed(original.newBuilder().headers(hb.build()).build());
        };
    }
}
