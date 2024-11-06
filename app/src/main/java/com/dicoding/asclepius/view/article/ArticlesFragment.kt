package com.dicoding.asclepius.view.article

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.FragmentArticlesBinding

class ArticlesFragment : Fragment() {

    // viewbinding
    private var _binding: FragmentArticlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentArticlesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel
        val viewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[ArticlesViewModel::class.java]

        // observe
        viewModel.listNews.observe(viewLifecycleOwner) { news ->
            setNewsList(news)
        }

        // observe viewmodel untuk ui loading atau progresbar
        viewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        // observe eror message jika tidak ada internet
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // function untuk visibility dari progresbar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setNewsList(news: List<ArticlesItem>) {
        val adapter = ArticlesAdapter()
        binding.rvNews.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvNews.adapter = adapter
        adapter.submitList(news)
    }

}