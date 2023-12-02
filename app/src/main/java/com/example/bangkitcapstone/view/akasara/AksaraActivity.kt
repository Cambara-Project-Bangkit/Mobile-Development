package com.example.bangkitcapstone.view.akasara

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.data.result.Result
import com.example.bangkitcapstone.databinding.ActivityAksaraBinding
import com.example.bangkitcapstone.view.adapter.AksaraAdapter

class AksaraActivity : AppCompatActivity() {

    private val viewModel by viewModels<AksaraViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityAksaraBinding
    private lateinit var adapter: AksaraAdapter

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

        // Observe the search result
        viewModel.searchResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Handle loading state
                }
                is Result.Success -> {
                    val aksaraList = result.data
                    // Update your adapter with the new list
                    adapter.submitList(aksaraList)
                }
                is Result.Error -> {
                    // Handle error state
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
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val aksara = item.data
                        setAksara(aksara)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Failed to Load", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setAksara(listAksara: List<AksaraItem>) {
        adapter.submitList(listAksara)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

