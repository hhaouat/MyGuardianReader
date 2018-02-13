package com.myguardianreader.articles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.R;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.myguardianreader.articles.favorite.SharedPreferencesFavorite;
import com.myguardianreader.articles.model.ArticlesFilter;
import com.myguardianreader.common.Event;
import com.myguardianreader.details.DetailFragment;
import com.reader.android.articles.model.Article;
import com.reader.android.articles.model.Header;
import com.reader.android.articles.model.Item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ArticlesFragment extends Fragment implements ArticlesPresenter.View {

    @BindView(R.id.articles_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.articles_swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ArticlesPresenter presenter;
    private ArticleAdapter adapter;

    private PublishSubject<Article> onClickArticle = PublishSubject.create();
    private ArticlesFilter articlesFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles_list, container, false);

        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //getActivity().setSupportActionBar(toolbar);

        presenter = HeadlinesApp.from(getActivity().getApplicationContext()).inject(this.getActivity());

        articlesFilter = new ArticlesFilter(presenter.getDBfavorites());

        adapter = new ArticleAdapter(this.getActivity(), onClickArticle);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unregister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        articlesFilter.closeConnection();
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void displayArticles(List<Article> articles) {
        List<Article> favoriteArticle = articlesFilter.setFavorite();
        List<Item> itemList = articlesFilter.createChronologicalList(articles, favoriteArticle);

        adapter.showArticles(itemList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayMessage(String errorMessage) {
        Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
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
    public void openArticleDetailActivity(Article article) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("article", article);
        detailFragment.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, detailFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
