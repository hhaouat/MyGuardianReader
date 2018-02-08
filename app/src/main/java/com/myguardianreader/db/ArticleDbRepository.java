package com.myguardianreader.db;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.reader.android.articles.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleDbRepository implements LoaderManager.LoaderCallbacks<Cursor> {

    private DatabaseHelper databaseHelper;
    private Context context;

    public ArticleDbRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public void saveArticles(List<Article> articles) {
        SQLiteDatabase sqlitedatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Article article : articles) {
            values.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID, article.getId());
            values.put(ArticleContract.ArticleEntry.COLUMN_THUMBNAIL, article.getThumbnail());
            values.put(ArticleContract.ArticleEntry.COLUMN_SECTION_ID, article.getSectionId());
            values.put(ArticleContract.ArticleEntry.COLUMN_SECTION_NAME, article.getSectionName());
            values.put(ArticleContract.ArticleEntry.COLUMN_PUBLISHED, article.getPublished());
            values.put(ArticleContract.ArticleEntry.COLUMN_TITLE, article.getTitle());
            values.put(ArticleContract.ArticleEntry.COLUMN_URL, article.getUrl());

            long newRowId = sqlitedatabase.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, values);
        }
    }
    public void saveFavoriteArticle(Article article) {
        SQLiteDatabase sqlitedatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID, article.getId());
        values.put(ArticleContract.ArticleEntry.COLUMN_THUMBNAIL, article.getThumbnail());
        values.put(ArticleContract.ArticleEntry.COLUMN_SECTION_ID, article.getSectionId());
        values.put(ArticleContract.ArticleEntry.COLUMN_SECTION_NAME, article.getSectionName());
        values.put(ArticleContract.ArticleEntry.COLUMN_PUBLISHED, article.getPublished());
        values.put(ArticleContract.ArticleEntry.COLUMN_TITLE, article.getTitle());
        values.put(ArticleContract.ArticleEntry.COLUMN_URL, article.getUrl());

        long newRowId = sqlitedatabase.insert(ArticleContract.ArticleEntry.TABLE_NAME, null, values);
    }

    public void closeConnection() {
        databaseHelper.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID,
                ArticleContract.ArticleEntry.COLUMN_THUMBNAIL,
                ArticleContract.ArticleEntry.COLUMN_SECTION_ID,
                ArticleContract.ArticleEntry.COLUMN_SECTION_NAME,
                ArticleContract.ArticleEntry.COLUMN_PUBLISHED,
                ArticleContract.ArticleEntry.COLUMN_TITLE,
                ArticleContract.ArticleEntry.COLUMN_URL
        };

        return new CursorLoader(context,
                ArticleContract.ArticleEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public List<Article> restoreFavoriteArticle() {

        String selectQuery = "SELECT * FROM " + ArticleContract.ArticleEntry.TABLE_NAME;
        final SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        List datas = new ArrayList<>();
        while (cursor.moveToNext()) {
            datas.add(createArticle(cursor));
        }
        cursor.close();

        return datas;
    }

    @NonNull
    private Article createArticle(Cursor cursor) {
        Article article = new Article(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getLong(5),
                cursor.getString(6),
                cursor.getString(7),
                true,
                0);
        return article;
    }

    public void removeFavoriteArticle(Article article) {
        SQLiteDatabase sqlitedatabase = databaseHelper.getWritableDatabase();
        sqlitedatabase.delete(ArticleContract.ArticleEntry.TABLE_NAME, ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID + " = ?", new String[] { article.getId()});
    }
}
