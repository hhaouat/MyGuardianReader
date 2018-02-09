package com.myguardianreader.articles.favorite;

import android.content.Context;

import com.myguardianreader.api.model.Favorite;
import com.myguardianreader.repository.local.ArticleDbRepository;
import com.reader.android.articles.model.Article;

import java.util.List;
import java.util.Set;

public class DbFavorites implements Favorite {

    private ArticleDbRepository articleDbRepository;

    public DbFavorites(Context context){
        articleDbRepository = new ArticleDbRepository(context);
    }

    @Override
    public void storeFavorite(Set<String> articleFavorite) {

    }

    @Override
    public void storeFavorite(Article articleFavorite) {
        articleDbRepository.saveFavoriteArticle(articleFavorite);
    }

    @Override
    public void setFavorite(String articleId) {

    }

    @Override
    public Set<String> getFavoriteSet() {
        return null;
    }

    @Override
    public List<Article> getFavoriteArticle() {
        return articleDbRepository.restoreFavoriteArticle();
    }

    @Override
    public void removeFavorite(String articleId) {

    }

    @Override
    public void removeFavorite(Article article) {
        articleDbRepository.removeFavoriteArticle(article);
    }

    public void closeConnection() {
        articleDbRepository.closeConnection();
    }
}
