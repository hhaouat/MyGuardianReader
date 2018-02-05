package com.myguardianreader.articles;

import android.util.Log;

import com.myguardianreader.common.BasePresenter;
import com.myguardianreader.common.BasePresenterView;
import com.reader.android.articles.model.Article;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

class ArticlesPresenter extends BasePresenter<ArticlesPresenter.View> {
    private final Scheduler uiScheduler;
    private final Scheduler ioScheduler;
    private final ArticlesRepository articlesRepository;

    private static final String TAG = ArticlesPresenter.class.getName();

    ArticlesPresenter(Scheduler uiScheduler, Scheduler ioScheduler, ArticlesRepository articlesRepository) {
        this.uiScheduler = uiScheduler;
        this.ioScheduler = ioScheduler;
        this.articlesRepository = articlesRepository;
    }

    @Override
    public void register(View view) {
        super.register(view);
    }

    interface View extends BasePresenterView {
        void showRefreshing(boolean isRefreshing);

        void displayArticles(List<Article> articles);

        void displayMessage(String errorMessage);

        Observable<Article> onArticleClicked();

        Observable<Object> onRefreshAction();

    }
}