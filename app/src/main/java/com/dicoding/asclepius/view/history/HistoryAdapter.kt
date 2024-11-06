package com.dicoding.asclepius.view.history

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.database.room.HistoryEntity
import com.dicoding.asclepius.databinding.ItemArticlesBinding
import com.dicoding.asclepius.view.article.ArticlesAdapter.MyViewHolder

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val historyList = mutableListOf<HistoryEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<HistoryEntity>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    class HistoryViewHolder(private val binding: ItemArticlesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(history: HistoryEntity){
            // Bind data ke view
            binding.tvTitle.text = history.label
            binding.tvAuthor.text = "Score: ${history.score}"

            // mendapatkan inputStream dari URI
            val imageUri = Uri.parse(history.imageUri)
            try {
                val inputStream = itemView.context.contentResolver.openInputStream(imageUri)
                Glide.with(binding.imgItemPhoto.context)
                    .load(inputStream)
                    .into(binding.imgItemPhoto)
            }catch (e: SecurityException){
                Log.e("Image Load Error", "Tidak memiliki izin untuk mengakses URI: ${history.imageUri}", e)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }
}