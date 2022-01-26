package com.bassem.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bassem.newsapp.model.Article
import java.util.concurrent.Executors

@Database(entities = [Article::class], version = 1)
abstract class ArticlesDataBase : RoomDatabase() {

    abstract fun dao(): DAO

    companion object {
        var INSTANCE: ArticlesDataBase? = null
        val NUMBER_OF_THREADS = 4
        val dataBaseWriteExutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)
        fun getInstance(context: Context): ArticlesDataBase {
            if (INSTANCE == null) {
                kotlin.synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context!!.applicationContext,
                        ArticlesDataBase::class.java,
                        "Article_database"
                    ).build()
                    INSTANCE = instance
                }
            }

            return INSTANCE!!
        }
    }
}