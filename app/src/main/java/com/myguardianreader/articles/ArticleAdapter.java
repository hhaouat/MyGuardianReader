package com.myguardianreader.articles;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.myguardianreader.R;
import com.myguardianreader.common.GlideCircleTransformation;
import com.reader.android.articles.model.Article;
import com.reader.android.articles.model.Header;
import com.reader.android.articles.model.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Item> articles = new ArrayList<>();
    private static Context context;
    private PublishSubject<Article> onClickArticle;

    public ArticleAdapter(Context context, PublishSubject<Article> onClickArticle) {
        this.context = context;
        this.onClickArticle = onClickArticle;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_article, parent, false);
        ArticleViewHolder articleViewHolder = new ArticleViewHolder(view);

        switch (viewType){
            case Article.ARTICLE_TYPE:
                RxView.clicks(view)
                        .map(new Function<Object, Article>() {
                            @Override
                            public Article apply(Object ignored) throws Exception {
                                Article entity = (Article) articles.get(articleViewHolder.getAdapterPosition());
                                Log.d("RxView.clicks: ", "" + entity);
                                return entity;
                            }
                        })
                        .subscribeWith(onClickArticle);
                return articleViewHolder;

            case Article.HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_type, parent, false);
                return new HeaderViewHolder(view);
        }
        return articleViewHolder;
    }

    @Override
    public int getItemViewType(int position) {

        if (articles.get(position) instanceof Article) {
            return Article.ARTICLE_TYPE;
        }else {
            return Article.HEADER_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = articles.get(position);
        if (item!= null) {
            if (item instanceof Article) {
                if (position != 0) {
                    ((ArticleViewHolder) holder).bind((Article)articles.get(position));
                }
            }else{
                ((HeaderViewHolder) holder).bind((Header)articles.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    void showArticles(List<Item> articles) {
        this.articles.clear();
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.article_item)
        CardView article_item_cardView;

        @BindView(R.id.article_thumbnail_imageview)
        ImageView thumbnailImageView;

        @BindView(R.id.article_headline_textview)
        TextView headlineTextView;

        @BindView(R.id.article_date)
        TextView dateTextView;

        ArticleViewHolder(View view) {
            super(view);
        }
        void bind(Article article) {
            ButterKnife.bind(this, itemView);
            headlineTextView.setText(article.getTitle());
            Glide.with(context)
                    .load(article.getThumbnail())
                    .bitmapTransform(new GlideCircleTransformation(context))
                    .into(thumbnailImageView);

            Date dateArticlePublished = new Date(article.getPublished());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

            dateTextView.setText(dateFormat.format(dateArticlePublished));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.header_textview)
        TextView headerTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
        void bind(Header header) {
            ButterKnife.bind(this, itemView);
            headerTextView.setText(header.getName());
        }
    }
}
