package com.android.news.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,    // we need a type converter for source class since room can only handle basic data types
    val title: String,
    val url: String,
    val urlToImage: String
)