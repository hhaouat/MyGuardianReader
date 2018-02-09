package com.myguardianreader.repository.remote;

import com.reader.android.api.model.ApiArticleListResponse;
import com.reader.android.api.model.ApiArticleResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GuardianClient {
    @GET("search?show-fields=headline,thumbnail&page-size=50")
    Single<ApiArticleListResponse> searchArticles(@Query("q") String searchTerm);

    @GET
    Single<ApiArticleResponse> getArticle(@Url String articleUrl, @Query("show-fields") String fields);
}
