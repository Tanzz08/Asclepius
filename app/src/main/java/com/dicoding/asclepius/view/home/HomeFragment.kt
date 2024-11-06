package com.dicoding.asclepius.view.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.database.room.HistoryEntity
import com.dicoding.asclepius.databinding.FragmentHomeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat




class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private lateinit var imageClassifierHelper: ImageClassifierHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root




        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        val viewModel: MainViewModel by viewModels {
            factory
        }

        // setclicklistener button
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            binding.progressIndicator.visibility = View.VISIBLE
            analyzeImage()

            // ambil data setelah analisis
//            val data = HistoryEntity(
//                result =
//            )


        }

        // insialisasi classifierHelper
        imageClassifierHelper = ImageClassifierHelper(
            context = requireActivity(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        showToast(error)
                    }
                }

                override fun onResults(results: List<Classifications>?) {
                    activity?.runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()){
                                Log.d("Classification Result", it.toString())

                                val sortedCategories = it[0].categories.sortedByDescending { it.score }
                                val topCategory = it[0].categories[0]
                                val displayResult = sortedCategories.joinToString("\n") { category ->
                                    "${category.label}: ${NumberFormat.getPercentInstance().format(category.score)}"
                                }

                                val historyData = HistoryEntity(
                                    label = topCategory.label,
                                    score = topCategory.score,
                                    imageUri = currentImageUri.toString()
                                )

                                // insert data ke database
                                viewModel.insert(historyData)


                                moveToResult(displayResult)
                            }else {
                                showToast("Tidak dapat menemukan Hasil")
                            }
                        }
                    }
                }

            }
        )

    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            binding.analyzeButton.isEnabled = true
        } else {
//            showToast(getString(R.string.empty_image_warning))
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        binding.progressIndicator.visibility = View.VISIBLE

        // memastikan gambar telah dipilih sebelum klasifikasi
        currentImageUri?.let { uri ->

            imageClassifierHelper.classifyStaticImage(uri)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun moveToResult(result: String) {
        val intent = Intent(requireActivity(), ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, result)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

}