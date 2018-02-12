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
    private DbFavorites dbFavorites;
    private PublishSubject<Article> onClickArticle = PublishSubject.create();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles_list, container, false);

        ButterKnife.bind(this, view);
        //recyclerView = (RecyclerView) view.findViewById(R.id.articles_recyclerview);
        //swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.articles_swiperefreshlayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //getActivity().setSupportActionBar(toolbar);

        presenter = HeadlinesApp.from(getActivity().getApplicationContext()).inject(this.getActivity());

        dbFavorites = new DbFavorites(this.getActivity());

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
        dbFavorites.closeConnection();
    }

    @Override
    public void showRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void displayArticles(List<Article> articles) {
        List<Article> favoriteArticle = setFavorite();
        List<Item> itemList = createChronologicalList(articles, favoriteArticle);

        adapter.showArticles(itemList);
        adapter.notifyDataSetChanged();
    }

    private List<Article> setFavorite(){

        SharedPreferencesFavorite sharedPreferencesFavorite = HeadlinesApp.getSharedPreferences(getActivity());
        Set<String> favoriteSet = sharedPreferencesFavorite.getFavoriteSet();

        if (dbFavorites.getFavoriteArticle().isEmpty()){
            return new ArrayList<>();
        }
        else{
            List<Article> articleList = dbFavorites.getFavoriteArticle();
            return articleList;
        }
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
