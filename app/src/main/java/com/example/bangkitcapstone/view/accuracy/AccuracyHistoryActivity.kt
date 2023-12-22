package com.example.bangkitcapstone.view.accuracy

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.ViewModelFactory
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.local.pref.UserPreferences
import com.example.bangkitcapstone.data.local.pref.dataStore
import com.example.bangkitcapstone.databinding.ActivityAccuracyHistoryBinding
import com.example.bangkitcapstone.view.adapter.AccuracyHistoryAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AccuracyHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccuracyHistoryBinding
    private val viewModel by viewModels<AccuracyHistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var adapter: AccuracyHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccuracyHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this@AccuracyHistoryActivity)
        binding.rvAksara.layoutManager = layoutManager

        adapter = AccuracyHistoryAdapter(onDeleteClickListener = { accuracyHistory ->
            showDeleteConfirmationDialog(accuracyHistory)
        })
        binding.rvAksara.adapter = adapter

        viewModel.getAccuracyHistory(getUserIdFromUserPreferences()).observe(this) { accuracyHistory ->
            accuracyHistory(accuracyHistory)
        }

        setupAction()
    }

    private fun accuracyHistory(list: List<AccuracyHistory>) {
        adapter.submitList(list)
    }

    private fun getUserIdFromUserPreferences(): String {
        val userPreferences = UserPreferences.getInstance(applicationContext.dataStore)
        return runBlocking {
            userPreferences.getSession().first().token
        }
    }

    private fun setupAction() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.accuracy_high -> {
                    viewModel.getAllHighAccuracyHistory(getUserIdFromUserPreferences()).observe(this) { accuracyHistory ->
                        accuracyHistory(accuracyHistory)
                    }
                    true
                }
                R.id.accuracy_low -> {
                    viewModel.getAllLowAccuracyHistory(getUserIdFromUserPreferences()).observe(this){ accuracyHistory ->
                        accuracyHistory(accuracyHistory)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showDeleteConfirmationDialog(accuracyHistory: AccuracyHistory) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus item ini?")
            .setPositiveButton("Ya") { dialog, which ->
                viewModel.deleteAccuracyHistory(accuracyHistory)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
