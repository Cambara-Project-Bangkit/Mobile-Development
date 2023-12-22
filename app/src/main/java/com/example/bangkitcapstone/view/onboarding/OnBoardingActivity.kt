package com.example.bangkitcapstone.view.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bangkitcapstone.databinding.ActivityOnBoardingBinding
import com.example.bangkitcapstone.view.login.LoginActivity

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager2.adapter = ViewPagerAdapter(this)

        setupButtonClickListeners()
    }

    private fun setupButtonClickListeners() {


        binding.nextButton.text = getNextButtonText(binding.viewPager2.currentItem)

        binding.nextButton.setOnClickListener {
            val nextIndex = binding.viewPager2.currentItem + 1
            if (nextIndex < binding.viewPager2.adapter?.itemCount ?: 0) {
                binding.viewPager2.setCurrentItem(nextIndex, true)
                binding.nextButton.text = getNextButtonText(nextIndex)
            } else {
                navigateToLogin()
            }
        }

        binding.backButton.setOnClickListener {
            val prevIndex = binding.viewPager2.currentItem - 1
            if (prevIndex >= 0) {
                binding.viewPager2.setCurrentItem(prevIndex, true)
                binding.nextButton.text = getNextButtonText(prevIndex)
            }
        }

        binding.skipButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getNextButtonText(position: Int): String {
        return when (position) {
            0, 1 -> "Next"
            2 -> "Finish"
            else -> "Next"
        }
    }
}
