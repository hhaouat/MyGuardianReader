package com.myguardianreader.articles;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.myguardianreader.articles.favorite.SharedPreferencesFavorite;
import com.myguardianreader.common.Event;
import com.myguardianreader.details.DetailsActivity;
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
    private DbFavorites dbFavorites;

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
        dbFavorites = new DbFavorites(articlesActivity);
        articlesActivity.setSupportActionBar(toolbar);
        adapter = new ArticleAdapter(articlesActivity, onClickArticle);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void displayArticles(List<Article> articles) {

        List<Article> favoriteArticle = setFavorite();
        List<Item> itemList = createChronologicalList(articles, favoriteArticle);

        adapter.showArticles(itemList);
        adapter.notifyDataSetChanged();
    }

    private List<Item> createChronologicalList(List<Article> articles, List<Article> favoriteArticleGroup) {
        List<Article> currentWeekGroup = new ArrayList<>();
        List<Article> lastWeekGroup = new ArrayList<>();

        createGroups(articles, favoriteArticleGroup, currentWeekGroup, lastWeekGroup);

        return groupSubListsInOne(favoriteArticleGroup, currentWeekGroup, lastWeekGroup);
    }

    private void createGroups(List<Article> articles, List<Article> favoriteArticleGroup,
                              List<Article> currentWeekGroup, List<Article> lastWeekGroup) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar itemCalendar    = Calendar.getInstance();
        Calendar lastWeekCalendar = getLastWeekCalendar();

        sortListArticlesByDate(articles);

        for (Article article : articles) {

            if (favoriteArticleGroup.contains(article))
                continue;

            itemCalendar.setTime(new Date(article.getPublished()));

            if (isArticleBelongingToTheWeekCalendar(itemCalendar, currentCalendar)){
                currentWeekGroup.add(article);
            } else {
                if(isArticleBelongingToTheWeekCalendar(itemCalendar, lastWeekCalendar))
                    lastWeekGroup.add(article);
            }
        }
    }

    @Override
    public void displayMessage(String errorMessage) {
        Toast.makeText(articlesActivity.getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    private List<Article> setFavorite(){

        SharedPreferencesFavorite sharedPreferencesFavorite = HeadlinesApp.getSharedPreferences(articlesActivity);
        Set<String> favoriteSet = sharedPreferencesFavorite.getFavoriteSet();

        if (dbFavorites.getFavoriteArticle().isEmpty()){
            return new ArrayList<>();
        }
        else{
            List<Article> articleList = dbFavorites.getFavoriteArticle();
            return articleList;
        }
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

    private void sortListArticlesByDate(List<Article> articles) {
        Collections.sort(articles);
    }

    private boolean isArticleBelongingToTheWeekCalendar(Calendar itemCalendar, Calendar weekCalendar) {
        return itemCalendar.get(itemCalendar.WEEK_OF_YEAR) == weekCalendar.get(weekCalendar.WEEK_OF_YEAR) ? true : false;
    }

    private Calendar getLastWeekCalendar() {
        Calendar lastWeekCalendar = Calendar.getInstance();

        int i = lastWeekCalendar.get(Calendar.DAY_OF_WEEK) - lastWeekCalendar.getFirstDayOfWeek();
        lastWeekCalendar.add(Calendar.DATE, -i - 7);

        return lastWeekCalendar;
    }

    private List<Item> groupSubListsInOne(List<Article> favoriteArticle,
                                          List<Article> currentWeekList,
                                          List<Article> lastWeekList) {

        List<Item> filtredArticle = new ArrayList<>();
        int indexFiltredArticle = 0;

        indexFiltredArticle = addFavoriteToListIfExist(filtredArticle, favoriteArticle);

        filtredArticle.add(indexFiltredArticle, new Header("This week"));
        filtredArticle.addAll(currentWeekList);
        filtredArticle.add(new Header("Last week"));
        filtredArticle.addAll(lastWeekList);
        return filtredArticle;
    }

    private int addFavoriteToListIfExist(List<Item> filtredArticle, List<Article> favoriteArticle) {
        int indexFiltredArticle;
        if (favoriteArticle.size() != 0) {
            filtredArticle.add(0, new Header("Favorite"));
            filtredArticle.addAll(favoriteArticle);
            indexFiltredArticle = favoriteArticle.size() + 1;
        }
        else{
            indexFiltredArticle = 0;
        }
        return indexFiltredArticle;
    }

    @Override
    public void openArticleDetailActivity(Article article) {
        Intent intent = new Intent(articlesActivity, DetailsActivity.class);
        intent.putExtra("article", article);
        articlesActivity.startActivity(intent);
    }

    public void closeConnection() {
        dbFavorites.closeConnection();
    }
}
