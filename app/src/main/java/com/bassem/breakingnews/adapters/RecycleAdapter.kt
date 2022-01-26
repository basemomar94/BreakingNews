package com.bassem.newsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bassem.breakingnews.R
import com.bassem.newsapp.model.Article
import com.bumptech.glide.Glide

class RecycleAdapter(
    val list: MutableList<Article>,
    val context: Context,
    val listner:OnclickListner

) : RecyclerView.Adapter<RecycleAdapter.ViewHolder>() {


    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val title = itemview.findViewById<TextView>(R.id.tvTitle)
        val description = itemview.findViewById<TextView>(R.id.tvDescription)
        val published = itemview.findViewById<TextView>(R.id.tvPublishedAt)
        val image = itemview.findViewById<ImageView>(R.id.ivArticleImage)
        init {
            itemview.setOnClickListener {
                val position:Int=adapterPosition
                listner.onClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_preview, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = list[position]
        holder.title.text = article.title
        holder.description.text = article.description
        holder.published.text = article.publishedAt
        Glide.with(context).load(article.urlToImage).into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnclickListner{

        fun onClick (position:Int)

    }
}