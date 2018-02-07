package com.myguardianreader.articles;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesActivity extends AppCompatActivity {

    public static final String TAG = "com.myguardianreader.articles.ArticlesActivity";
    @BindView(R.id.articles_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.articles_swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ArticlesDisplayer articlesDisplayer;
    private ArticlesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_list);

        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        articlesDisplayer = new ArticlesDisplayer(this, recyclerView,
                swipeRefreshLayout,
                toolbar);
        presenter = HeadlinesApp.from(getApplicationContext()).inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.register(articlesDisplayer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
