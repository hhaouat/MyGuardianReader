package com.reader.android.articles.model

import android.os.Parcel
import android.os.Parcelable

data class Article (
        val id: String?,
        val thumbnail: String?,
        val sectionId: String?,
        val sectionName: String?,
        val published: Long,
        val title: String?,
        val url: String?,
        var favorite: Boolean,
        var type: Int) : Parcelable, Comparable<Article>, Item() {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt() != 0,
            parcel.readInt()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(thumbnail)
        dest.writeString(sectionId)
        dest.writeString(sectionName)
        dest.writeLong(published)
        dest.writeString(title)
        dest.writeString(url)
        dest.writeInt(if (favorite) 1 else 0)
        dest.writeInt(type)
    }

    override fun compareTo(article: Article): Int = article.published!!.compareTo(this.published)

    companion object {
        const val ARTICLE_TYPE : Int = 0
        const val HEADER_TYPE : Int = 1

        @JvmField
        val CREATOR: Parcelable.Creator<Article> = object : Parcelable.Creator<Article>
        {
            override fun createFromParcel(parcel: Parcel): Article {
                return Article(parcel)
            }

            override fun newArray(size: Int): Array<Article?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun equals(other: Any?)= other is Article && other.id == id

    override fun hashCode()= id!!.hashCode()
}

