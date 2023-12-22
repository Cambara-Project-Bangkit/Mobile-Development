package com.example.bangkitcapstone.view.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.data.local.pref.UserModel
import com.example.bangkitcapstone.data.result.Result
import com.example.bangkitcapstone.databinding.ActivityLoginBinding
import com.example.bangkitcapstone.view.custom.CustomEmailEditText
import com.example.bangkitcapstone.view.custom.CustomPasswordEditText
import com.example.bangkitcapstone.view.main.MainActivity
import com.example.bangkitcapstone.view.signup.SignupActivity

class LoginActivity : AppCompatActivity() {


    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, )
    }

    private lateinit var binding: ActivityLoginBinding

    private lateinit var passwordEditText: CustomPasswordEditText
    private lateinit var emailEditText: CustomEmailEditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEditText = binding.edLoginEmail
        passwordEditText = binding.edLoginPassword
        loginButton = binding.loginButton

        emailEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        setMyButtonEnable()

        passwordEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        navigateToRegister()

        setupView()
        setupAction()

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val user = UserModel(
                                token = result.data.user.token,
                                email = email
                            )
                            viewModel.saveSession(user)
                            showSuccessPopup()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }
                    }
                }
            }
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

    private fun navigateToRegister(){
        binding.signupButtonButton.setOnClickListener{
            startActivity((Intent(this, SignupActivity::class.java)))
            finish()
        }

    }

    private fun setMyButtonEnable() {
        val password = passwordEditText.text
        val email = emailEditText.text
        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()

        loginButton.isEnabled = password != null && password.toString().length >= 8 && email != null && validEmail
        if (!loginButton.isEnabled){
            binding.loginButton.alpha = 0.3f
        } else {
            binding.loginButton.alpha = 1f
        }

    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog() {
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.congrats)
            val loginSucceed = getString(R.string.login_succeed)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(loginSucceed)
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val message = dialog.findViewById<TextView>(R.id.text_result_succes)
        message.text = "Berhasil Masuk"


        val btnClose = dialog.findViewById<Button>(R.id.btn_close_result_success)
        btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        dialog.show()
    }
}
