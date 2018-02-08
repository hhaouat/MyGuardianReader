package com.myguardianreader.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.R;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.reader.android.articles.model.Article;

public class DetailsActivity extends Activity {

    private static final String TAG = "DetailsActivity";
    private DetailsPresenter detailsPresenter;
    private DetailsDisplayer detailsDisplayer;
    private String articleUrl;
    private DbFavorites dbFavorites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Article article = getIntent().getParcelableExtra("article");
        articleUrl = article.getUrl();

        dbFavorites = new DbFavorites(this);
        detailsDisplayer = new DetailsDisplayer(this, getIntent(), dbFavorites);

        detailsPresenter = HeadlinesApp.from(getApplicationContext()).injectDetails(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        detailsPresenter.register(detailsDisplayer, articleUrl);
        Log.d(TAG,"onResume DetailsActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbFavorites.closeConnection();
        Log.d(TAG,"onDestroy DetailsActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        detailsPresenter.unregister();
        Log.d(TAG,"onPause DetailsActivity");
    }
}
