package com.android.news.repository

import com.android.news.api.RetrofitInstance
import com.android.news.database.ArticleDatabase
import com.android.news.models.Article
import com.android.news.models.NewsResponse
import retrofit2.Response

class NewsRepository(
    val database: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)
    }

    suspend fun searchNews(searchQuery: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.searchForNews(searchQuery, pageNumber)
    }

    suspend fun upsert(article: Article)=database.getArticleDao().upsert(article)

    fun getArticles()=database.getArticleDao().getArticles()

    suspend fun delete(article: Article)=database.getArticleDao().deleteArticle(article)
}