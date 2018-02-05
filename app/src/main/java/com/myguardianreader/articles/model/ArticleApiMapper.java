package com.myguardianreader.articles.model;

import com.reader.android.api.model.ApiArticleContent;
import com.reader.android.api.model.ApiArticleResponse;

public class ArticleApiMapper {

    public ApiArticleContent map(ApiArticleResponse apiArticleResponse) {
        return apiArticleResponse.getResponse();
    }

}
