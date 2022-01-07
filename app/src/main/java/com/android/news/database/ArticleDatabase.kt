package com.android.news.database

import android.content.Context
import androidx.room.*
import com.android.news.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase(){

    abstract fun getArticleDao():ArticleDao

    companion object{
        @Volatile // this means that if one thread changes this variable other thread will observe those changes immediately
        private var instance:ArticleDatabase?=null
        private val LOCK=Any()

        operator fun invoke(context:Context)= instance?: synchronized(LOCK){         // this function is called whenever we call instantiate an object

            // We write synchronized so that only one thread can set it at once
            instance?:createDatabase(context).also{
                instance=it
            }
        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
    }
}