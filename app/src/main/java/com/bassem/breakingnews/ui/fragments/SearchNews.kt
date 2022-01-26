package com.bassem.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.breakingnews.R
import com.bassem.breakingnews.databinding.FragmentSearchNewsBinding
import com.bassem.newsapp.adapters.RecycleAdapter
import com.bassem.newsapp.model.Article
import com.bassem.newsapp.model.NewsResponse
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class SearchNews : Fragment(R.layout.fragment_search_news), RecycleAdapter.OnclickListner {
    var _binding: FragmentSearchNewsBinding? = null
    val binding get() = _binding
    var searchRV: RecyclerView? = null
    var searchAdapter: RecycleAdapter? = null
    var newsList: MutableList<Article>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsList = arrayListOf()
        recycleSetup(newsList!!, requireContext())
        binding!!.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding?.shimmer?.visibility=View.VISIBLE
                newsList?.clear()
                getData(p0!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })


    }


    fun recycleSetup(list: MutableList<Article>, context: Context) {
        searchAdapter = RecycleAdapter(list, context, this)
        searchRV = requireView().findViewById(R.id.rvSearchNews)
        searchRV?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    override fun onClick(position: Int) {
        val article = newsList!![position]
        val url = article.url
        val bundle: Bundle = Bundle()
        bundle.putString("url", url)
        bundle.putSerializable("article", article)
        println(url)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.newsNavHostFragment)
        navController.navigate(R.id.action_searchNews_to_itemArticle, bundle)

    }

    private fun getData(query: String) {
        var list: MutableList<Article>? = null
        val API_KEY = "e5095abc9bd84a259c6f0dba9a733416"
        val url =
            "https://newsapi.org/v2/everything?q=$query&from=2022-01-25&to=2022-01-25&sortBy=popularity&apiKey=$API_KEY"
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
                                newsList?.add(article)
                            }
                            println(newsList!!.size)
                            activity!!.runOnUiThread {

                                binding?.shimmer?.apply {
                                    visibility = View.GONE
                                    stopShimmer()
                                }
                                binding?.rvSearchNews?.visibility = View.VISIBLE

                                searchAdapter?.notifyDataSetChanged()
                            }


                        }
                    }
                })
            } catch (E: Exception) {
                println(E.message)
            }
        }).start()


    }


}