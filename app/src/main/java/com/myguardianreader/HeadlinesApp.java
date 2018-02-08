package com.myguardianreader;

import android.app.Application;
import android.content.Context;

import com.myguardianreader.articles.ArticlesModule;
import com.myguardianreader.articles.favorite.SharedPreferencesFavorite;

public class HeadlinesApp extends Application {
    private final ArticlesModule articlesModule = new ArticlesModule();

    public static ArticlesModule from(Context applicationContext) {
        return ((HeadlinesApp) applicationContext).articlesModule;
    }

    public static SharedPreferencesFavorite getSharedPreferences(Context context){
        return SharedPreferencesFavorite.newInstance(context);
    }
}
