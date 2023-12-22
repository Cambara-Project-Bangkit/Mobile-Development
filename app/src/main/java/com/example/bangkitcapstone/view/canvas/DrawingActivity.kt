package com.example.bangkitcapstone.view.canvas

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.databinding.ActivityDrawingBinding
import com.example.bangkitcapstone.view.main.MainActivity

class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = binding.drawingView

        binding.btnClear.setOnClickListener {
            drawingView.clearDrawing()
        }

        binding.btnSaveGallery.setOnClickListener {
            drawingView.saveDrawingToGallery(this)
            showResultPopup()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_info_drawing -> {
                    showInfoPopup()
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showResultPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.result_dialog_drawing)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val message = dialog.findViewById<TextView>(R.id.text_result)

        message.text = "Berhasil Menyimpan Gambar"


        val btnClose = dialog.findViewById<Button>(R.id.btn_close_result_drawing)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showInfoPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.popup_info_drawing)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val message1 = dialog.findViewById<TextView>(R.id.text_information_drawing_1)
        message1.text = getString(R.string.text_information_drawing_1)

        val message2 = dialog.findViewById<TextView>(R.id.text_information_drawing_2)
        message2.text = getString(R.string.text_information_drawing_2)

        val message3 = dialog.findViewById<TextView>(R.id.text_information_drawing_3)
        message3.text = getString(R.string.text_information_drawing_3)

        val btnClose = dialog.findViewById<Button>(R.id.btn_close)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}
