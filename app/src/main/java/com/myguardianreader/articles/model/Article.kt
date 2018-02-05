package com.reader.android.articles.model

class Article (
        val id: String?,
        val thumbnail: String?,
        val sectionId: String?,
        val sectionName: String?,
        val published: Long,
        val title: String?,
        val url: String?,
        var favorite: Boolean,
        var type: Int)

