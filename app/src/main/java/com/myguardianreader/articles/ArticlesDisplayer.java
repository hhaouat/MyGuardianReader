package com.myguardianreader.articles;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.myguardianreader.common.Event;
import com.reader.android.articles.model.Article;
import com.reader.android.articles.model.Header;
import com.reader.android.articles.model.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class ArticlesDisplayer implements ArticlesPresenter.View {

    private static final String TAG = "ArticlesDisplayer";
    private final Toolbar toolbar;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private ArticleAdapter adapter;
    ArticlesActivity articlesActivity;
    private PublishSubject<Article> onClickArticle = PublishSubject.create();

    public ArticlesDisplayer(ArticlesActivity articlesActivity,
                             RecyclerView recyclerView,
                             SwipeRefreshLayout swipeRefreshLayout,
                             Toolbar toolbar) {

        this.toolbar = toolbar;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.articlesActivity = articlesActivity;
        adapter = new ArticleAdapter(articlesActivity, onClickArticle);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void displayArticles(List<Article> articles) {
        adapter.showArticles(articles);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayMessage(String errorMessage) {
        Toast.makeText(articlesActivity.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public Observable<Article> onArticleClicked() {
        return onClickArticle;
    }

    @Override
    public Observable<Object> onRefreshAction() {
        return Observable.create(emitter -> {
            swipeRefreshLayout.setOnRefreshListener(() -> emitter.onNext(0));
            emitter.setCancellable(() -> swipeRefreshLayout.setOnRefreshListener(null));
        }).startWith(Event.IGNORE);
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

}
