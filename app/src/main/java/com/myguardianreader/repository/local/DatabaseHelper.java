package com.myguardianreader.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "guardianreader.db";
    private static final int DATABASE_VERSION = 1;
    private static final String _ID = "_id";

    private static final String[] TABLES = {ArticleContract.ArticleEntry.TABLE_NAME};
    private static final String[][] COLUMNS = {
            {
                    ArticleContract.ArticleEntry.COLUMN_ARTICLE_ID + " TEXT UNIQUE",
                    ArticleContract.ArticleEntry.COLUMN_THUMBNAIL+ " TEXT",
                    ArticleContract.ArticleEntry.COLUMN_SECTION_ID+ " TEXT",
                    ArticleContract.ArticleEntry.COLUMN_SECTION_NAME+ " TEXT",
                    ArticleContract.ArticleEntry.COLUMN_PUBLISHED+ " TEXT",
                    ArticleContract.ArticleEntry.COLUMN_TITLE+ " TEXT",
                    ArticleContract.ArticleEntry.COLUMN_URL + " TEXT",
            },
    };

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        for (int i = 0; i < TABLES.length; i++) {
            String table = TABLES[i];
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            sb.append(table);
            sb.append(" ("+_ID +" INTEGER PRIMARY KEY AUTOINCREMENT");

            for (int j = 0; j < COLUMNS[i].length; j++) {
                sb.append(",");
                sb.append(COLUMNS[i][j]);
            }
            sb.append(")");
            db.execSQL(sb.toString());
        }
    }

    private void dropTables(SQLiteDatabase db) {
        for (String table : TABLES) {
            String sql = "DROP TABLE IF EXISTS " + table;
            db.execSQL(sql);
        }
    }
}
