package com.bassem.newsapp.ui.fragments

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
import com.bassem.breakingnews.databinding.FragmentBreakingNewsBinding
import com.bassem.newsapp.adapters.RecycleAdapter
import com.bassem.newsapp.model.Article
import com.bassem.newsapp.model.NewsResponse
import com.bassem.newsapp.ui.viewmodels.BreakingNewsViewModel
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
    lateinit var viewModel: ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[BreakingNewsViewModel::class.java]
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
        getData()
    }

    private fun getData() {
        var list: MutableList<Article>? = null
        val API_KEY = "e5095abc9bd84a259c6f0dba9a733416"
        val url =
            "https://newsapi.org/v2/top-headlines?country=eg&apiKey=$API_KEY"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        Thread(Runnable {
            try {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        var result: String? = null
                        if (!response.isSuccessful) {
                            println("${response.message}=========ERROR")
                        } else {
                            val responseBody: ResponseBody? = response.body
                            if (responseBody != null) {
                                result = responseBody.string()

                            }

                            val gson = GsonBuilder().create()
                            val newsResponse = gson.fromJson(result, NewsResponse::class.java)
                            list = newsResponse.articles as MutableList<Article>
                            for (article in list!!) {
                                articleList.add(article)
                            }
                            activity!!.runOnUiThread {

                                binding?.shimmer?.apply {
                                    visibility = View.GONE
                                    stopShimmer()
                                }
                                binding?.rvBreakingNews?.visibility = View.VISIBLE

                                newsAdapter.notifyDataSetChanged()
                            }


                        }
                    }
                })
            } catch (E: Exception) {
                println(E.message)
            }
        }).start()


    }

    fun rvSetup(list: MutableList<Article>, context: Context) {
        newsAdapter = RecycleAdapter(list, context, this)
        rvBreakingNews = requireView().findViewById(R.id.rvBreakingNews)
        rvBreakingNews.setHasFixedSize(true)
        rvBreakingNews.layoutManager = LinearLayoutManager(context)
        rvBreakingNews.adapter = newsAdapter
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

}