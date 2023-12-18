package com.example.bangkitcapstone.view.onboarding.screen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.databinding.ActivityOnBoardingBinding
import com.example.bangkitcapstone.databinding.FragmentThirdScreenBinding
import com.example.bangkitcapstone.view.login.LoginActivity


class ThirdScreen : Fragment() {

    private lateinit var binding: FragmentThirdScreenBinding
    private lateinit var _binding: ActivityOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdScreenBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        navigateToLoginScreen()
    }
//
//    private fun navigateToLoginScreen() {
//        _binding.b.setOnClickListener {
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
//        }
//    }
}

