package com.myguardianreader.articles.model;

import com.reader.android.api.model.ApiArticle;
import com.reader.android.api.model.ApiArticleListResponse;
import com.reader.android.articles.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleMapper {

    public List<Article> map(ApiArticleListResponse apiArticleListResponse) {
        List<Article> articles = new ArrayList<>();

        for (ApiArticle apiArticle : apiArticleListResponse.getResponse().getResults()) {
            articles.add(new Article(apiArticle.getId(),
                    apiArticle.getFields().getThumbnail(),
                    apiArticle.getSectionId(),
                    apiArticle.getSectionName(),
                    apiArticle.getWebPublicationDate().getTime(),
                    apiArticle.getFields().getHeadline(),
                    apiArticle.getApiUrl(),
                    false,
                    0)
            );
        }

        return articles;
    }
}
