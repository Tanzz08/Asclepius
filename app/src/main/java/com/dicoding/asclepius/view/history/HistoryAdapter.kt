package com.dicoding.asclepius.view.history

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.database.room.HistoryEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import java.text.NumberFormat

class HistoryAdapter(private val onDeleteClick: (HistoryEntity) -> Unit): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val historyList = mutableListOf<HistoryEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<HistoryEntity>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(history: HistoryEntity){
            // Bind data ke view
            binding.tvTitle.text = history.label
            binding.tvAuthor.text = "Confidence Score: ${NumberFormat.getPercentInstance().format(history.score)}"

            val imageUri = Uri.parse(history.imageUri)// parse URI untuk mendapatkan gambar
            try {
                Glide.with(binding.imgItemPhoto.context)
                    .load(imageUri)
                    .into(binding.imgItemPhoto)
            }catch (e: SecurityException){
                Log.e("Image Load Error", "Tidak memiliki izin untuk mengakses URI: ${history.imageUri}", e)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])

        val ivDelete = holder.binding.btnDelete
        ivDelete.setOnClickListener {
            onDeleteClick(historyList[position])
        }
    }
}