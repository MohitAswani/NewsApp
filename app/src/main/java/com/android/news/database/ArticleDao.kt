package com.android.news.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.news.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article):Long

    @Query("SELECT * FROM articles")
    fun getArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}