package com.bassem.newsapp.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.breakingnews.R
import com.bassem.breakingnews.api.NewsApi
import com.bassem.breakingnews.databinding.FragmentSearchNewsBinding
import com.bassem.newsapp.adapters.RecycleAdapter
import com.bassem.newsapp.model.Article
import com.bassem.newsapp.model.NewsResponse
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate

class SearchNews : Fragment(R.layout.fragment_search_news), RecycleAdapter.OnclickListner {
    var _binding: FragmentSearchNewsBinding? = null
    val binding get() = _binding
    var searchRV: RecyclerView? = null
    var searchAdapter: RecycleAdapter? = null
    var newsList: MutableList<Article>? = null
    val API_KEY = "e5095abc9bd84a259c6f0dba9a733416"


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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(p0: String?): Boolean {
                binding?.shimmer?.visibility = View.VISIBLE
                newsList?.clear()
                getSearch(p0!!)
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun getSearch(search: String) {
        val fromDate = LocalDate.now().minusMonths(1).toString()
        val call = NewsApi.create().searchNews(search, fromDate, "popularity", API_KEY)
        call.enqueue(object : retrofit2.Callback<NewsResponse?> {
            override fun onResponse(
                call: retrofit2.Call<NewsResponse?>,
                response: retrofit2.Response<NewsResponse?>
            ) {
                val apiList = response.body()?.articles
                for (article in apiList!!) {
                    newsList?.add(article)
                }
                binding?.shimmer?.apply {
                    visibility = View.GONE
                    stopShimmer()
                }
                binding?.rvSearchNews?.visibility = View.VISIBLE
                searchAdapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: retrofit2.Call<NewsResponse?>, t: Throwable) {
            }
        })
    }


}