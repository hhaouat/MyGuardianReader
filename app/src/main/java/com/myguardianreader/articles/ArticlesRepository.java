package com.myguardianreader.articles;

import com.myguardianreader.articles.model.ArticleApiMapper;
import com.myguardianreader.articles.model.ArticleMapper;
import com.myguardianreader.common.GuardianService;
import com.reader.android.api.model.ApiArticleContent;
import com.reader.android.articles.model.Article;

import java.util.List;

import io.reactivex.Single;

public class ArticlesRepository {
    private final GuardianService guardianService;
    private final ArticleMapper articleMapper;
    private final ArticleApiMapper articleApiMapper;

    public ArticlesRepository(GuardianService guardianService, ArticleMapper articleMapper, ArticleApiMapper articleApiMapper) {
        this.guardianService = guardianService;
        this.articleMapper = articleMapper;
        this.articleApiMapper = articleApiMapper;
    }

    public Single<List<Article>> latestFintechArticles() {
        return guardianService.searchArticles("fintech,brexit").map(articleMapper::map);
    }

    public Single<ApiArticleContent> getArticle(String articleUrl) {
        return guardianService.getArticle(articleUrl, "main,body,headline,thumbnail").map(articleApiMapper::map);
    }
}
