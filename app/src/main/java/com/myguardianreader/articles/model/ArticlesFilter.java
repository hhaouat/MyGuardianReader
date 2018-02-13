package com.myguardianreader.articles.model;

import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.myguardianreader.articles.favorite.SharedPreferencesFavorite;
import com.reader.android.articles.model.Article;
import com.reader.android.articles.model.Header;
import com.reader.android.articles.model.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ArticlesFilter {

    private DbFavorites dbFavorites;

    public ArticlesFilter(DbFavorites dbFavorites){
        this.dbFavorites = dbFavorites;
    }

    public List<Article> setFavorite(){

        if (dbFavorites.getFavoriteArticle().isEmpty()){
            return new ArrayList<>();
        }
        else{
            List<Article> articleList = dbFavorites.getFavoriteArticle();
            return articleList;
        }
    }

    public List<Item> createChronologicalList(List<Article> articles, List<Article> favoriteArticleGroup) {
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

    private void sortListArticlesByDate(List<Article> articles) {
        Collections.sort(articles);
    }

    private boolean isArticleBelongingToTheWeekCalendar(Calendar itemCalendar, Calendar weekCalendar) {
        return itemCalendar.get(itemCalendar.WEEK_OF_YEAR) == weekCalendar.get(weekCalendar.WEEK_OF_YEAR) ? true : false;
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

    private Calendar getLastWeekCalendar() {
        Calendar lastWeekCalendar = Calendar.getInstance();

        int i = lastWeekCalendar.get(Calendar.DAY_OF_WEEK) - lastWeekCalendar.getFirstDayOfWeek();
        lastWeekCalendar.add(Calendar.DATE, -i - 7);

        return lastWeekCalendar;
    }

    public void closeConnection() {
        dbFavorites.closeConnection();
    }
}
