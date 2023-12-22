package com.example.bangkitcapstone.view.signup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.databinding.ActivitySignupBinding
import com.example.bangkitcapstone.view.custom.CustomEmailEditText
import com.example.bangkitcapstone.view.custom.CustomPasswordEditText
import com.example.bangkitcapstone.data.result.Result
import com.example.bangkitcapstone.view.akasara.AksaraActivity
import com.example.bangkitcapstone.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySignupBinding

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: CustomEmailEditText
    private lateinit var passwordEditText: CustomPasswordEditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameEditText = binding.edRegisterName
        emailEditText = binding.edRegisterEmail
        passwordEditText = binding.edRegisterPassword
        signupButton = binding.signupButton

        //validasi nama
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                if (nameEditText.text.isEmpty()) {
                    nameEditText.error = "Nama tidak boleh kosong!"
                } else {
                    nameEditText.error = null
                }
                setMyButtonEnable()

            }

        })

        //validasi email
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

        })

        //validasi password

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

        })

        setupView()
        setupAction()
    }

    private fun setMyButtonEnable() {
        val password = passwordEditText.text
        val email = emailEditText.text.toString()
        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

        signupButton.isEnabled =
            password != null && password.toString().length >= 8 && email != null && validEmail
        if (!signupButton.isEnabled) {
            binding.signupButton.alpha = 0.3f
        } else {
            binding.signupButton.alpha = 1f
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
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.register(name, email, password).observe(this){ result ->
                when(result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        alertDialog()
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog() {
        Log.d("SignupActivity", "alertDialog() called")
        AlertDialog.Builder(this).apply {
            val title = getString(R.string.congrats)
            val accountCreated = getString(R.string.account_created)
            val next = getString(R.string.next)

            setTitle(title)
            setMessage(accountCreated)
            setPositiveButton(next) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun navigateToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
