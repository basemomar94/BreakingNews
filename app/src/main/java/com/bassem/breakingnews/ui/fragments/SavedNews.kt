package com.bassem.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.breakingnews.R
import com.bassem.breakingnews.databinding.FragmentSavedNewsBinding
import com.bassem.newsapp.adapters.RecycleAdapter
import com.bassem.newsapp.db.ArticlesDataBase
import com.bassem.newsapp.model.Article
import com.google.android.material.snackbar.Snackbar

class SavedNews : Fragment(R.layout.fragment_saved_news), RecycleAdapter.OnclickListner {
    var _binding: FragmentSavedNewsBinding? = null
    val binding get() = _binding
    lateinit var rvBreakingNews: RecyclerView
    lateinit var newsAdapter: RecycleAdapter
    lateinit var articleList: MutableList<Article>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleList = arrayListOf()
        rvSetup(articleList, requireContext())
        getData()
        val itemTouchHelperCallbacks =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val article = articleList[position]
                    deleteArticle(article, position)
                }

            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallbacks)
        itemTouchHelper.attachToRecyclerView(binding?.rvSavedNews)
    }

    fun rvSetup(list: MutableList<Article>, context: Context) {
        newsAdapter = RecycleAdapter(list, context, this)
        rvBreakingNews = requireView().findViewById(R.id.rvSavedNews)
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
        println(url)
        val navController =
            Navigation.findNavController(requireActivity(), R.id.newsNavHostFragment)
        navController.navigate(R.id.action_savedNews_to_itemArticle, bundle)
    }

    private fun getData() {
        val db = ArticlesDataBase.getInstance(requireContext())
        ArticlesDataBase.dataBaseWriteExutor.execute {
            val dataBaseList = db.dao().getArticles()
            for (article in dataBaseList) {
                articleList.add(article)
            }
            activity?.runOnUiThread {
                newsAdapter.notifyDataSetChanged()
            }
        }


    }

    fun deleteArticle(article: Article, position: Int) {
        val db = ArticlesDataBase.getInstance(requireContext())
        ArticlesDataBase.dataBaseWriteExutor.execute {
            db.dao().delete(article)
            articleList.remove(article)

        }
        newsAdapter.notifyItemRemoved(position)
        Snackbar.make(requireView(), "Article removed", Snackbar.LENGTH_LONG).apply {
            setAction("Undo") {
                saveArticle(article, position)

            }
        }.show()
    }

    fun saveArticle(article: Article, position: Int) {
        val db = ArticlesDataBase.getInstance(requireContext())
        ArticlesDataBase.dataBaseWriteExutor.execute {
            db.dao().saveArticle(article)
        }
        articleList.add(position, article)
        newsAdapter.notifyItemInserted(position)
    }

}