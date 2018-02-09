package com.myguardianreader.repository

import com.myguardianreader.articles.favorite.DbFavorites
import com.myguardianreader.repository.remote.GuardianService
import com.reader.android.articles.model.Article
import io.reactivex.Scheduler
import io.reactivex.Single

class GuardianRepository(private val guardianService: GuardianService,
                         private val ioScheduler: Scheduler,
                         private val dbFavorites: DbFavorites) {

    fun getFintechArticlesList(): Single<List<Article>> {
        return guardianService.latestFintechArticles().subscribeOn(ioScheduler)
    }
}