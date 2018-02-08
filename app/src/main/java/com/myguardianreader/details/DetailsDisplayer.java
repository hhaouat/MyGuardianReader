package com.myguardianreader.details;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

public class DetailsDisplayer implements DetailsPresenter.View {

    private final DetailsActivity detailsActivity;
    private final DbFavorites dbFavorites;
    private View.OnClickListener listener;
    private FavoriteState favoriteState;
    private ProgressDialog progressDialog;

    private SharedPreferencesFavorite sharedPreferencesFavorite;
    private Article article;

    private static final String TAG = "DetailsDisplayer";

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

    public DetailsDisplayer(DetailsActivity detailsActivity, Intent intent, DbFavorites dbFavorites) {
        this.detailsActivity = detailsActivity;
        this.dbFavorites = dbFavorites;

        detailsActivity.setContentView(R.layout.activity_detail);
        ButterKnife.bind(this, detailsActivity);

        sharedPreferencesFavorite = HeadlinesApp.getSharedPreferences(detailsActivity);

        displayProgressDialog();

        article = intent.getParcelableExtra("article");

        Glide.with(this.detailsActivity).load(article.getThumbnail()).into(thumbnail);
        title.setText(article.getTitle());

        displayDate(date, article.getPublished());
        displayFavorite(detailsActivity, favoriteIndicator);
    }

    private void displayProgressDialog() {
        progressDialog = new ProgressDialog(detailsActivity);
        progressDialog.setMessage("Loading");
        progressDialog.show();
    }


    private void displayDate(TextView date, Long articlePublished) {
        Date dateArticlePublished = new Date(articlePublished);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        date.setText(dateFormat.format(dateArticlePublished));
    }

    private void displayFavorite(DetailsActivity detailsActivity, ImageView favoriteIndicator) {
        favoriteState = new FavoriteState();

        favoriteIndicator.setImageDrawable(detailsActivity.getDrawable(isFavorite() == true ? R.mipmap.ic_favorite_white : R.mipmap.ic_favorite_border_white));

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
                favoriteIndicator.setImageDrawable(getFavoriteDrawable(detailsActivity, favoriteState.getStatus()));
            }
        };
        favoriteIndicator.setOnClickListener(listener);
    }

    private boolean isFavorite() {
        return article.getFavorite();
    }

    private Drawable getFavoriteDrawable(DetailsActivity detailsActivity, FavoriteStatus favoriteStatus) {
        return detailsActivity.getDrawable(favoriteStatus == FavoriteStatus.FAVOURITE ?
                R.mipmap.ic_favorite_white : R.mipmap.ic_favorite_border_white);
    }

    @Override
    public void showDetails(ApiArticleContent apiArticleContent) {
        apiArticleContent.getContent().getFields().getBody();

        body.setText(Html.fromHtml(apiArticleContent.getContent().getFields().getBody()));
    }

    @Override
    public void displayMessage(String errorMessage) {
        Toast.makeText(detailsActivity, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}