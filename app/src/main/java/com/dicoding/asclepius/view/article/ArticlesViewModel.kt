package com.dicoding.asclepius.view.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.response.NewsHealthResponse
import com.dicoding.asclepius.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticlesViewModel : ViewModel() {
    // livedata
    private val _listNews = MutableLiveData<List<ArticlesItem>>()
    val listNews: LiveData<List<ArticlesItem>> = _listNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    companion object{
        private const val TAG = "ArticlesViewModel"
        private const val API_KEY = "8a6a328fa0bc45868b3dd9b70353aba5"
    }

    init {
        getHeadlineNews()
    }

    // fungsi untuk mengambil data dari api
    private fun getHeadlineNews(){
        _isLoading.value = true

        val client = ApiConfig.getApiService().getNews(API_KEY)
        client.enqueue(object : Callback<NewsHealthResponse> {
            override fun onResponse(
                call: Call<NewsHealthResponse>,
                response: Response<NewsHealthResponse>,
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _listNews.value = response.body()?.articles
                }else {
                    val errorMessage = "Error: ${response.message()} (code ${response.code()})"
                    _errorMessage.value = errorMessage
                    Log.e(TAG, errorMessage)
                }
            }

            override fun onFailure(call: Call<NewsHealthResponse>, t: Throwable) {
                _isLoading.value = false
                val errorMessage = "Network Failure: ${t.message.toString()}"
                _errorMessage.value = errorMessage
                Log.e(TAG, errorMessage)
            }

        })
    }
}