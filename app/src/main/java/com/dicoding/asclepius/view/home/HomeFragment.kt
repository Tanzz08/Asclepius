package com.dicoding.asclepius.view.home

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
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
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.database.room.HistoryEntity
import com.dicoding.asclepius.databinding.FragmentHomeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.dicoding.asclepius.viewmodel.ViewModelFactory
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat




class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private lateinit var viewModel: MainViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.croppedImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }

        // setclicklistener button
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {

            analyzeImage()
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

                                val label = it[0].categories[0].label
                                val score = NumberFormat.getPercentInstance().format(it[0].categories[0].score).trim()
                                val topCategory = it[0].categories[0]
                                val displayResult = "$label : $score"

                                val historyData = HistoryEntity(
                                    label = topCategory.label,
                                    score = topCategory.score,
                                    imageUri = viewModel.currentImageUri.toString()
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

    // mendapatkan hasil uCrop
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            viewModel.croppedImageUri = UCrop.getOutput(data!!)
            showImage()
            binding.analyzeButton.isEnabled = true
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.let { Log.e("UCrop Error", it.message.toString()) }
        } else if (resultCode == RESULT_CANCELED && requestCode == UCrop.REQUEST_CROP) {
            // Menangani kasus cancel: reset URI dan nonaktifkan tombol analyze
            viewModel.croppedImageUri = null
            binding.analyzeButton.isEnabled = false
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            startCrop(uri)
        } else {
//            showToast(getString(R.string.empty_image_warning))
            Log.d("Photo Picker", "No media selected")
        }
    }

    // fungsi untuk memulai uCrop
    private fun startCrop(uri: Uri){
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .start(requireContext(), this)
    }


    private fun showImage() {
        viewModel.croppedImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(null)
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        binding.progressIndicator.visibility = View.VISIBLE

        // memastikan gambar telah dipilih sebelum klasifikasi
        viewModel.currentImageUri?.let { uri ->

            imageClassifierHelper.classifyStaticImage(uri)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun moveToResult(result: String) {
        val intent = Intent(requireActivity(), ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, viewModel.croppedImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, result)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

}