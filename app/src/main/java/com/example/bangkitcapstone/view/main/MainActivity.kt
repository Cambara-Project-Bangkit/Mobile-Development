package com.example.bangkitcapstone.view.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.databinding.ActivityMainBinding
import com.example.bangkitcapstone.view.akasara.AksaraActivity
import com.example.bangkitcapstone.view.canvas.DrawingActivity
import com.example.bangkitcapstone.view.onboarding.OnBoardingActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, )
    }


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity((Intent(this, OnBoardingActivity::class.java)))
                finish()
            } else {

            }

            setupAction()
            setupView()
        }
        binding.buttonDrawing.setOnClickListener {
            startActivity(Intent(this, DrawingActivity::class.java))
            finish()
        }
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
               R.id.menu1 -> {
                   showDialog()
                   true
               }
                else -> {false}
            }
        }

        binding.buttonAksara.setOnClickListener {
            startActivity(Intent(this, AksaraActivity::class.java))
            finish()
        }
    }


    private fun logout(){
        viewModel.logout()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val title = getString(R.string.logout)
        val confirmation = getString(R.string.logout_confirmation)
        val yes = getString(R.string.yes)
        val no = getString(R.string.no)

        builder.setTitle(title)
        builder.setMessage(confirmation)

        builder.setPositiveButton(yes) { dialogInterface: DialogInterface, i: Int ->
            logout()
            finish()
        }

        builder.setNegativeButton(no) { dialogInterface: DialogInterface, i: Int ->
            dialogInterface.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}