package com.myguardianreader.details;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.myguardianreader.HeadlinesApp;
import com.myguardianreader.R;
import com.myguardianreader.articles.favorite.DbFavorites;
import com.myguardianreader.articles.favorite.SharedPreferencesFavorite;
import com.reader.android.api.model.ApiArticleContent;
import com.reader.android.api.model.FavoriteState;
import com.reader.android.api.model.FavoriteStatus;
import com.reader.android.articles.model.Article;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment implements DetailsPresenter.View{

    private DetailsPresenter detailsPresenter;
    private String articleUrl;
    private DbFavorites dbFavorites;
    private ProgressDialog progressDialog;
    private FavoriteState favoriteState;
    private View.OnClickListener listener;
    private SharedPreferencesFavorite sharedPreferencesFavorite;
    private Article article;

    private static final String TAG = "DetailFragment";

    @BindView(R.id.detail_thumbnail)
    ImageView thumbnail;

    @BindView(R.id.detail_title)
    TextView title;

    @BindView(R.id.detail_body)
    TextView body;

    @BindView(R.id.detail_date)
    TextView date;

    @BindView(R.id.favouriteIndicator)
    ImageView favoriteIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        dbFavorites = new DbFavorites(getActivity());

        ButterKnife.bind(this, view);

        detailsPresenter = HeadlinesApp.from(getActivity().getApplicationContext()).injectDetails(getActivity());

        sharedPreferencesFavorite = HeadlinesApp.getSharedPreferences(getActivity());

        displayProgressDialog();

        Bundle args = getArguments();
        article = args.getParcelable("article");
        articleUrl = article.getUrl();

        Glide.with(this.getActivity()).load(article.getThumbnail()).into(thumbnail);
        title.setText(article.getTitle());

        displayDate(date, article.getPublished());
        displayFavorite(favoriteIndicator);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            article = args.getParcelable("article");
            articleUrl = article.getUrl();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        detailsPresenter.register(this, articleUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbFavorites.closeConnection();
    }

    @Override
    public void onPause() {
        super.onPause();
        detailsPresenter.unregister();
    }

    private void displayProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }


    private void displayDate(TextView date, Long articlePublished) {
        Date dateArticlePublished = new Date(articlePublished);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        date.setText(dateFormat.format(dateArticlePublished));
    }

    private void displayFavorite(ImageView favoriteIndicator) {
        favoriteState = new FavoriteState();

        favoriteIndicator.setImageDrawable(getActivity().getDrawable(isFavorite() == true ? R.mipmap.ic_favorite_white : R.mipmap.ic_favorite_border_white));

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OnClick View");
                FavoriteStatus favoriteStatus = favoriteState.getStatus();
                if (favoriteStatus == FavoriteStatus.FAVOURITE) {
                    favoriteState.setStatus(FavoriteStatus.UN_FAVOURITE);
                    sharedPreferencesFavorite.removeFavorite(article.getId());
                    dbFavorites.removeFavorite(article);
                }else{
                    favoriteState.setStatus(FavoriteStatus.FAVOURITE);
                    sharedPreferencesFavorite.setFavorite(article.getId());
                    dbFavorites.storeFavorite(article);
                }
                favoriteIndicator.setImageDrawable(getFavoriteDrawable(favoriteState.getStatus()));
            }
        };
        favoriteIndicator.setOnClickListener(listener);
    }

    private boolean isFavorite() {
        return article.getFavorite();
    }

    private Drawable getFavoriteDrawable(FavoriteStatus favoriteStatus) {
        return getActivity().getDrawable(favoriteStatus == FavoriteStatus.FAVOURITE ?
                R.mipmap.ic_favorite_white : R.mipmap.ic_favorite_border_white);
    }

    @Override
    public void showDetails(ApiArticleContent apiArticleContent) {
        apiArticleContent.getContent().getFields().getBody();

        body.setText(Html.fromHtml(apiArticleContent.getContent().getFields().getBody()));
    }

    @Override
    public void displayMessage(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
