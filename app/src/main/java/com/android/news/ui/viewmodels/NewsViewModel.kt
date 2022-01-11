package com.android.news.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.news.NewsApplication
import com.android.news.models.Article
import com.android.news.models.NewsResponse
import com.android.news.repository.NewsRepository
import com.android.news.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val repository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews=MutableLiveData<Resource<NewsResponse>>()
    private var breakingNewsPage=1
    var breakingNewsResponse:NewsResponse?=null

    val searchNews=MutableLiveData<Resource<NewsResponse>>()
    var searchNewsPage=1
    var searchNewsResponse:NewsResponse?=null

    init{
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode:String)=viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>
    {
        if(response.isSuccessful){
            response.body()?.let {
                breakingNewsPage++
                if(breakingNewsResponse==null) {
                    breakingNewsResponse = it
                }
                else {
                    breakingNewsResponse?.articles?.addAll(it.articles)
                }

                return Resource.Success(breakingNewsResponse?:it)
            }
        }
        return Resource.Error(message = response.message())
    }

    fun searchNews(searchQuery:String)=viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>
    {
        if(response.isSuccessful){
            response.body()?.let {
                searchNewsPage++
                if(searchNewsResponse==null) {
                    searchNewsResponse = it
                }
                else {
                    searchNewsResponse?.articles?.addAll(it.articles)
                }

                return Resource.Success(searchNewsResponse?:it)
            }
        }
        return Resource.Error(message = response.message())
    }

    fun upsertArticle(article: Article)=viewModelScope.launch {
        repository.upsert(article)
    }

    fun getSavedArticles()=repository.getArticles()

    fun deleteArticle(article: Article)=viewModelScope.launch {
        repository.delete(article)
    }

    private fun hasInternetConnection():Boolean{
        // for checking the internet connectivity we need a connectivity manager which is system service which requires context and for that we need a context
        // So rather than using the activity context which gets destroyed when the activity get destroyed we use application context

        val connectivityManager=getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            val activeNetwork=connectivityManager.activeNetwork?:return false
            val capabilities=connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI)-> true
                capabilities.hasTransport(TRANSPORT_CELLULAR)-> true
                capabilities.hasTransport(TRANSPORT_ETHERNET)-> true
                else -> false
            }
        }
        else
        {
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    TYPE_WIFI-> true
                    TYPE_MOBILE-> true
                    TYPE_ETHERNET-> true
                    else-> false
                }
            }
        }
        return false
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response=repository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response=response))
            }
            else{
                breakingNews.postValue(Resource.Error(message = "No internet connection"))
            }

        }
        catch (t:Throwable)
        {
            when(t){
                is IOException-> breakingNews.postValue(Resource.Error(message = "Network failure"))
                else -> breakingNews.postValue(Resource.Error(message = "Conversion error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response=repository.searchNews(searchQuery,breakingNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response=response))
            }
            else{
                searchNews.postValue(Resource.Error(message = "No internet connection"))
            }

        }
        catch (t:Throwable)
        {
            when(t){
                is IOException-> searchNews.postValue(Resource.Error(message = "Network failure"))
                else -> searchNews.postValue(Resource.Error(message = "Conversion error"))
            }
        }
    }
}