package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object{
        const val KEY_NEWS = "key_news"
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get intent
        val article = if (Build.VERSION.SDK_INT <=30){
            intent.getParcelableExtra(KEY_NEWS, ArticlesItem::class.java)
        }else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(KEY_NEWS)
        }

        if (article != null){
            // display
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description
            binding.tvAuthor.text = article.author

            Glide.with(this)
                .load(article.urlToImage)
                .into(binding.ivCover)

            val formatDate = formatPublishedDate(article.publishedAt.toString())
            binding.tvName.text = "Published At: $formatDate"

        } else{
            Toast.makeText(this, "Data tidak ada atau dihapus", Toast.LENGTH_SHORT).show()
        }


    }

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