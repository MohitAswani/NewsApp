package com.android.news.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.news.databinding.ItemArticleBinding
import com.android.news.models.Article
import com.bumptech.glide.Glide

class BreakingNewsAdapter : RecyclerView.Adapter<BreakingNewsAdapter.NewsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemArticleBinding=ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(itemArticleBinding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.setArticle(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NewsViewHolder(private val binding:ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun setArticle(article: Article)
        {
            Glide.with(binding.root).load(article.urlToImage).into(binding.ivArticleImage)
            binding.tvSource.text=article.source.name
            binding.tvTitle.text=article.title
            binding.tvDescription.text=article.description
            binding.tvDate.text=article.publishedAt
            binding.tvDescription.setOnClickListener{
                onItemClickListener?.let{it(article)}
            }
        }
    }

    private var onItemClickListener:((Article)->Unit)?=null

    fun setOnItemClickListener(listener:(Article)->Unit){
        onItemClickListener=listener
    }

    private val differCallback= object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    private val differ = AsyncListDiffer(this,differCallback)
}