package com.dicoding.asclepius.view.article

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemArticlesBinding
import com.dicoding.asclepius.view.DetailActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ArticlesAdapter : ListAdapter<ArticlesItem, ArticlesAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(private val binding: ItemArticlesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article : ArticlesItem){
//            binding.tvName.text = article.source.name
            binding.tvTitle.text = article.title
            binding.tvAuthor.text = article.author

            Glide.with(itemView.context)
                .load(article.urlToImage)
                .into(binding.imgItemPhoto)

            val formatDate = formatPublishedDate(article.publishedAt.toString())
            binding.tvPublishAt.text = formatDate
        }

        // format tanggal
        private fun formatPublishedDate(publishedAt: String): String {
            return try {
                // Format input (ISO 8601)
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")

                // Format output
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                val date = inputFormat.parse(publishedAt)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                "Tanggal tidak valid"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intentDetail = Intent(context, DetailActivity::class.java)
            intentDetail.putExtra(DetailActivity.KEY_NEWS, article)
            context.startActivity(intentDetail)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}