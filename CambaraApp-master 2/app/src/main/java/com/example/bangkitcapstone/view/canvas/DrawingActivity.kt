package com.example.bangkitcapstone.view.canvas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        }

    }

    override fun onBackPressed() {
        // Add a call to the superclass implementation
        super.onBackPressed()

        // Create an Intent to navigate back to the main activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optional: Call finish() to close the DrawingActivity
    }

}
