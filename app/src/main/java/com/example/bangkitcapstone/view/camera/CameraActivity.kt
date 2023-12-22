package com.example.bangkitcapstone.view.camera

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
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
import com.example.bangkitcapstone.view.utils.Utils.reduceFileImage
import com.example.bangkitcapstone.view.utils.Utils.uriToFile
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity() {

    private var currentImageUri: Uri? = null
    private lateinit var customLoadingDialog: Dialog
    private var id: String? = null


    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(this,)
    }

    private lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAccuracyButtonEnabled(false)
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnAccuracy.setOnClickListener { uploadImage() }
        binding.btnGalery.setOnClickListener { startGallery() }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_sort -> {
                    startActivity(Intent(this, AccuracyHistoryActivity::class.java))
                    true
                }
                R.id.menu_info_camera -> {
                    showInfoPopup()
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
                        showLoading()
                        val detailResponse = result.data
                        setAksaraDetailData(detailResponse)
                        hideCustomLoading()
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

    private fun showLoading() {
        if (!::customLoadingDialog.isInitialized || !customLoadingDialog.isShowing) {
            customLoadingDialog = Dialog(this).apply {
                setContentView(R.layout.layout_custom_loading)
                setCancelable(false)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }
        }
    }

    private fun hideCustomLoading() {
        customLoadingDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }


    override fun onDestroy() {
        hideCustomLoading()
        super.onDestroy()
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
            setAccuracyButtonEnabled(true)
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image file", "showImage ${imageFile.path}")
            val aksara = binding.tvItemName.text.toString()

            viewModel.uploadAksara(aksara, imageFile).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading()
                    is Result.Success -> {
                        handleUploadResult(result)
                        hideCustomLoading()
                    }
                    is Result.Error -> {
                        showLoading()
                        showToast(result.error)
                        hideCustomLoading()
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

                    showResultDialog(accuracy, predictedAksara)

                    val aksara = binding.tvItemName.text.toString()
                    if (aksara == predictedAksara) {
                        viewModel.getSession().observe(this) { user ->
                            user?.let {
                                viewModel.viewModelScope.launch {
                                    val accuracyHistory = AccuracyHistory(
                                        userToken = it.token,
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

    private fun showResultDialog(accuracy: Double, predictedAksara: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.result_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val akurasiTextView = dialog.findViewById<TextView>(R.id.text_accuracy)
        val predictedAksaraTextView = dialog.findViewById<TextView>(R.id.text_aksara)

        akurasiTextView.text = "Akurasi: $accuracy"

        if (accuracy == 0.0) {
            akurasiTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_validation))
            predictedAksaraTextView.text = "Masukan Gambar yang Benar"
        } else if (accuracy < 50) {
            akurasiTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
            predictedAksaraTextView.text = "Gambar Aksara yang Kamu Kirim $predictedAksara"
        } else {
            akurasiTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
            predictedAksaraTextView.text = "Gambar Aksara yang Kamu Kirim $predictedAksara"
        }


        val btnClose = dialog.findViewById<Button>(R.id.btn_close_result)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showInfoPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.popup_info_camera)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val message1 = dialog.findViewById<TextView>(R.id.text_information_camera_1)
        val message2 = dialog.findViewById<TextView>(R.id.text_information_camera_2)
        val message3 = dialog.findViewById<TextView>(R.id.text_information_camera_3)
        message1.text = getString(R.string.text_information_camera_1)
        message2.text = getString(R.string.text_information_camera_2)
        message3.text = getString(R.string.text_information_camera_3)

        val btnClose = dialog.findViewById<Button>(R.id.btn_close)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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
        super.onBackPressed()

        val intent = Intent(this, AksaraActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setAccuracyButtonEnabled(enabled: Boolean) {
        binding.btnAccuracy.isEnabled = enabled
    }
}