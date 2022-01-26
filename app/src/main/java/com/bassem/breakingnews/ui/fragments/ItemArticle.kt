package com.bassem.newsapp.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bassem.breakingnews.R
import com.bassem.breakingnews.databinding.FragmentArticleBinding
import com.bassem.newsapp.db.ArticlesDataBase
import com.bassem.newsapp.model.Article
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ItemArticle : Fragment(R.layout.fragment_article) {

    var _binding: FragmentArticleBinding? = null
    val binding get() = _binding
    lateinit var article: Article


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       val bundle=this.arguments
        article= bundle!!.getSerializable("article") as Article

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding!!.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        println(article.title)
        binding?.webView!!.apply {
            webViewClient = WebViewClient()
            loadUrl(bundle!!.getString("url", "")!!)
        }

        binding!!.fab.setOnClickListener {
            saveArticle()
            println("tic")
        }






    }

    fun saveArticle(){
     val db=ArticlesDataBase.getInstance(requireContext())
        ArticlesDataBase.dataBaseWriteExutor.execute {
            db.dao().saveArticle(article)
        }
        Snackbar.make(requireView(),"Article has been saved",Snackbar.LENGTH_SHORT).show()
    }
}