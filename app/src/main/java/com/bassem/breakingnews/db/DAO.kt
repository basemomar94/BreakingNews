package com.bassem.newsapp.db

import androidx.room.*
import com.bassem.newsapp.model.Article

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveArticle(article: Article)

    @Query("SELECT * FROM articles")
    fun getArticles(): MutableList<Article>

    @Delete
    fun delete(article: Article)

}