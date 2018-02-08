package com.myguardianreader.api.model;

import com.reader.android.articles.model.Article;

import java.util.List;
import java.util.Set;

public interface Favorite {

    void storeFavorite(Set<String> articleFavorite);
    void storeFavorite(Article articleFavorite);
    void setFavorite(String articleId);
    Set<String> getFavoriteSet();
    List<Article> getFavoriteArticle();

    void removeFavorite(String articleId);
    void removeFavorite(Article article);
}
