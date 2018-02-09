package com.myguardianreader.repository.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class ArticleContract {

    public static final String CONTENT_AUTHORITY = "com.reader.android";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ARTICLE = "article";

    public static final class ArticleEntry implements BaseColumns {

        private static final String CONTENT_URI_STRING = "content://" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTICLE;

        public static final String TABLE_NAME = "article";
        public static final String COLUMN_ARTICLE_ID = "article_id";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_SECTION_ID = "section_id";
        public static final String COLUMN_SECTION_NAME = "section_name";
        public static final String COLUMN_PUBLISHED = "published";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";

        public static Uri buildPupilUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
