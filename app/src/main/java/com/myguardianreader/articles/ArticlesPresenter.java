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
        loadArticles(view);
        onArticleClicked(view);
    }

    void loadArticles(View view) {
        addToUnsubscribe(view.onRefreshAction()
                .doOnNext(ignored -> view.showRefreshing(true))
                .switchMapSingle(ignored -> articlesRepository.latestFintechArticles().subscribeOn(ioScheduler))
                .observeOn(uiScheduler)
                .subscribe(
                        articles -> {
                            Log.i(TAG, "Subscribe articles");
                            view.showRefreshing(false);
                            view.displayArticles(articles);
                            view.onArticleClicked();},
                        error -> {
                            Log.e(TAG,"Error subscribe" + error);
                            view.displayMessage("An error occurs while loading the data, please try again.");
                            view.showRefreshing(false);}));

    }

    private void onArticleClicked(View view) {
        addToUnsubscribe(view.onArticleClicked()
                .subscribe(
                        article -> view.openArticleDetailActivity(article),
                        error -> {
                            Log.e(TAG,"Error onArticleClicked");
                            view.displayMessage("An error occurs while loading the data, please try again.");}));

    }

    interface View extends BasePresenterView {
        void showRefreshing(boolean isRefreshing);

        void displayArticles(List<Article> articles);

        void displayMessage(String errorMessage);

        Observable<Article> onArticleClicked();

        Observable<Object> onRefreshAction();

        void openArticleDetailActivity(Article article);

    }
}