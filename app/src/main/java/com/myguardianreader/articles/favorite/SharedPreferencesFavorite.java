package com.myguardianreader.articles.favorite;

import android.content.Context;
import android.content.SharedPreferences;

import com.myguardianreader.api.model.Favorite;
import com.reader.android.articles.model.Article;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferencesFavorite implements Favorite {

    private static final String KEY_ARTICLE_ID = "article_id";
    private static final String ARTICLE_PREFERENCES = "priority_share_mru";

    private final SharedPreferences preferences;

    public static SharedPreferencesFavorite newInstance(Context context){
        SharedPreferences preferences = context.getSharedPreferences(ARTICLE_PREFERENCES, Context.MODE_PRIVATE);
        return new SharedPreferencesFavorite(preferences);
    }

    SharedPreferencesFavorite(SharedPreferences preferences){
        this.preferences = preferences;
    }

    @Override
    public void storeFavorite(Set<String> favoriteArticles) {
        preferences.edit()
                    .putStringSet(KEY_ARTICLE_ID, favoriteArticles)
                    .commit();
    }

    @Override
    public void storeFavorite(Article articleFavorite) {

    }

    @Override
    public void setFavorite(String articleId) {
        Set<String> favoriteArticle = new HashSet<>();
        if (preferences.contains(KEY_ARTICLE_ID)) {
            favoriteArticle = preferences.getStringSet(KEY_ARTICLE_ID, new HashSet<>());
        }
        if (favoriteArticle.contains(articleId)){
            return;
        }
        favoriteArticle.add(articleId);
        storeFavorite(favoriteArticle);
    }

    @Override
    public Set<String> getFavoriteSet() {
        if (preferences.contains(KEY_ARTICLE_ID)) {
            return preferences.getStringSet(KEY_ARTICLE_ID, new HashSet<>());
        }
        return new HashSet<>();
    }

    @Override
    public List<Article> getFavoriteArticle() {
        return null;
    }

    @Override
    public void removeFavorite(String articleId) {
        Set<String> favoriteArticle = preferences.getStringSet(KEY_ARTICLE_ID, new HashSet<>());
        favoriteArticle.remove(articleId);

        preferences.edit()
                .remove(KEY_ARTICLE_ID)
                .commit();

        preferences.edit()
                .putStringSet(KEY_ARTICLE_ID, favoriteArticle)
                .commit();

    }

    @Override
    public void removeFavorite(Article article) {

    }
}
