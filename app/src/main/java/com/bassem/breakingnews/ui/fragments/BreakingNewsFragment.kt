package com.bassem.breakingnews.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.breakingnews.R
import com.bassem.breakingnews.api.NewsApi
import com.bassem.breakingnews.databinding.FragmentBreakingNewsBinding
import com.bassem.newsapp.adapters.RecycleAdapter
import com.bassem.newsapp.model.Article
import com.bassem.newsapp.model.NewsResponse
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

import java.io.IOException


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news),
    RecycleAdapter.OnclickListner {
    var _binding: FragmentBreakingNewsBinding? = null
    val binding get() = _binding
    lateinit var rvBreakingNews: RecyclerView
    lateinit var newsAdapter: RecycleAdapter
    lateinit var articleList: MutableList<Article>
    val API_KEY = "e5095abc9bd84a259c6f0dba9a733416"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleList = arrayListOf()

        rvSetup(articleList, requireContext())
        getNews()
    }


    fun rvSetup(list: MutableList<Article>, context: Context) {
        newsAdapter = RecycleAdapter(list, context, this)
        rvBreakingNews = requireView().findViewById(R.id.rvBreakingNews)
        rvBreakingNews.setHasFixedSize(true)
        rvBreakingNews.layoutManager = LinearLayoutManager(context)
        rvBreakingNews.adapter = newsAdapter
        println("tre")
    }

    override fun onClick(position: Int) {
        val article = articleList[position]
        val url = article.url
        val bundle: Bundle = Bundle()
        bundle.putString("url", url)
        bundle.putSerializable("article", article)
        println(url)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.newsNavHostFragment)
        navController.navigate(R.id.action_breakingNewsFragment_to_itemArticle, bundle)


    }
    fun getNews() {
        val call = NewsApi.create().getBreakingNews("eg", API_KEY)
        call.enqueue(object : retrofit2.Callback<NewsResponse?> {
            override fun onResponse(
                call: retrofit2.Call<NewsResponse?>,
                response: retrofit2.Response<NewsResponse?>
            ) {
                val newsList = response.body()?.articles
                for (article in newsList!!) {
                    articleList.add(article)
                }

                binding?.shimmer?.apply {
                    visibility = View.GONE
                    stopShimmer()
                }
                binding?.rvBreakingNews?.visibility = View.VISIBLE
                newsAdapter.notifyDataSetChanged()



            }

            override fun onFailure(call: retrofit2.Call<NewsResponse?>, t: Throwable) {
                println("$t ============error")
            }
        })
    }

}