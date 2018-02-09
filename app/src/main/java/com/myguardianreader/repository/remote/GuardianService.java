package com.myguardianreader.repository.remote;

import com.myguardianreader.articles.model.ArticleApiMapper;
import com.myguardianreader.articles.model.ArticleMapper;
import com.myguardianreader.repository.remote.GuardianClient;
import com.reader.android.api.model.ApiArticleContent;
import com.reader.android.articles.model.Article;

import java.util.List;

import io.reactivex.Single;

public class GuardianService {
    private final GuardianClient guardianClient;
    private final ArticleMapper articleMapper;
    private final ArticleApiMapper articleApiMapper;

    public GuardianService(GuardianClient guardianClient, ArticleMapper articleMapper, ArticleApiMapper articleApiMapper) {
        this.guardianClient = guardianClient;
        this.articleMapper = articleMapper;
        this.articleApiMapper = articleApiMapper;
    }

    public Single<List<Article>> latestFintechArticles() {
        return guardianClient.searchArticles("fintech,brexit").map(articleMapper::map);
    }

    public Single<ApiArticleContent> getArticle(String articleUrl) {
        return guardianClient.getArticle(articleUrl, "main,body,headline,thumbnail").map(articleApiMapper::map);
    }
}
