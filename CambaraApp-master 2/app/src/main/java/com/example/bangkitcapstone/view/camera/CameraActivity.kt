package com.example.bangkitcapstone.view.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.remote.response.DetailAksaraResponse
import com.example.bangkitcapstone.data.remote.response.UploadResponse
import com.example.bangkitcapstone.databinding.ActivityCameraBinding
import com.example.bangkitcapstone.view.utils.Utils
import com.example.bangkitcapstone.data.result.Result
import com.example.bangkitcapstone.view.accuracy.AccuracyHistoryActivity
import com.example.bangkitcapstone.view.akasara.AksaraActivity
import com.example.bangkitcapstone.view.main.MainActivity
import com.example.bangkitcapstone.view.utils.Utils.reduceFileImage
import com.example.bangkitcapstone.view.utils.Utils.uriToFile
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null

    private var id: String? = null


    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnAccuracy.setOnClickListener { uploadImage() }
        binding.btnGalery.setOnClickListener { startGallery() }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_sort -> {
                    startActivity(Intent(this, AccuracyHistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }

        id = intent.getStringExtra(ID)

        viewModel.getAksaraDetail(id.toString()).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {

                    }

                    is Result.Error -> {

                    }

                    is Result.Success -> {
                        showLoading(false)
                        val detailResponse = result.data
                        setAksaraDetailData(detailResponse)
                    }
                }
            }
        }

    }

    private fun setAksaraDetailData(aksara: DetailAksaraResponse) {
        binding.ivItemPhoto.visibility = View.VISIBLE
        binding.apply {
            Glide.with(ivItemPhoto.context)
                .load(aksara.aksara.urlImage)
                .into(ivItemPhoto)
        }

        binding.tvItemName.visibility = View.VISIBLE
        binding.tvItemName.text = aksara.aksara.name



    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }

    private fun startCamera() {
        if (!allCameraPermmisionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS_CAMERA,
                REQUEST_CODE_CAMERA
            )
        }else {
            currentImageUri = Utils.getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // Ubah tipe argumen pada fungsi uploadImage
    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image file", "showImage ${imageFile.path}")
            val aksara = binding.tvItemName.text.toString()

            viewModel.uploadAksara(aksara, imageFile).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        handleUploadResult(result)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun handleUploadResult(result: Result<UploadResponse>) {
        when (result) {
            is Result.Success -> {
                val uploadResponse = result.data
                uploadResponse?.let {
                    val accuracy = it.data.accuracy.toDouble()
                    val predictedAksara = it.data.predicted_aksara
                    showToast("Unggah berhasil. Akurasi: $accuracy")
                    binding.edAccuracy.text = "Akurasi " + ": " + it.data.accuracy
                    if(accuracy != 0.0) {
                        binding.edNotes.text = "Gambar Aksara yang Kamu Kirim " + predictedAksara
                    } else {
                        binding.edNotes.text = "Masukan Gambar yang Benar"
                    }

                    val aksara = binding.tvItemName.text.toString()
                    if (aksara == predictedAksara) {
                        viewModel.getSession().observe(this) { user ->
                            user?.let {
                                viewModel.viewModelScope.launch {
                                    val accuracyHistory = AccuracyHistory(
                                        userId = it.token,
                                        accuracy = accuracy,
                                        predictedAksara = predictedAksara,
                                    )
                                    viewModel.insertAccuracyHistory(accuracyHistory)
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun allCameraPermmisionGranted() = REQUIRED_PERMISSIONS_CAMERA.all{
        ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val ID = ""
        val REQUIRED_PERMISSIONS_CAMERA = arrayOf(android.Manifest.permission.CAMERA)
        const val REQUEST_CODE_CAMERA = 10
        private const val AUTHOR = "com.example.bangkitcapstone"
    }

    override fun onBackPressed() {
        // Add a call to the superclass implementation
        super.onBackPressed()

        // Create an Intent to navigate back to the main activity
        val intent = Intent(this, AksaraActivity::class.java)
        startActivity(intent)
        finish() // Optional: Call finish() to close the DrawingActivity
    }

}