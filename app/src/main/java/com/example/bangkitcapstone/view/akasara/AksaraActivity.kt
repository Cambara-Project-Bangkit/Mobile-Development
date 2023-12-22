package com.example.bangkitcapstone.view.akasara

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.data.result.Result
import com.example.bangkitcapstone.databinding.ActivityAksaraBinding
import com.example.bangkitcapstone.view.adapter.AksaraAdapter
import com.example.bangkitcapstone.view.main.MainActivity

class AksaraActivity : AppCompatActivity() {

    private val viewModel by viewModels<AksaraViewModel> {
        ViewModelFactory.getInstance(this,)
    }

    private lateinit var binding: ActivityAksaraBinding
    private lateinit var adapter: AksaraAdapter
    private lateinit var customLoadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAksaraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvAksara.layoutManager = layoutManager

        adapter = AksaraAdapter(this@AksaraActivity)
        binding.rvAksara.adapter = adapter

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val aksara = searchView.text.toString()
                        if (aksara.isNotBlank()) {
                            viewModel.searchAksara(newQuery = aksara)
                        }
                        searchView.hide()
                        true
                    } else {
                        false
                    }
                }
        }

        viewModel.searchResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val aksaraList = result.data
                    adapter.submitList(aksaraList)
                }
                is Result.Error -> {
                    Toast.makeText(this, "Search failed:", Toast.LENGTH_SHORT).show()
                }
            }
        }

        getAksara()
    }

    private fun getAksara() {
        viewModel.getAksara().observe(this) { item ->
            if (item != null) {
                when (item) {
                    is Result.Loading -> {
                        showLoading()
                    }
                    is Result.Success -> {
                        hideCustomLoading()
                        val aksara = item.data
                        setAksara(aksara)
                    }
                    is Result.Error -> {
                        hideCustomLoading()
                        Toast.makeText(this, "Failed to Load", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setAksara(listAksara: List<AksaraItem>) {
        adapter.submitList(listAksara)
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

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

