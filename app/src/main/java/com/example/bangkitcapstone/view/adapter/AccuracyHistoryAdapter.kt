package com.example.bangkitcapstone.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bangkitcapstone.R
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.databinding.ItemAccuracyBinding

class AccuracyHistoryAdapter(private val onDeleteClickListener: (AccuracyHistory) -> Unit) : ListAdapter<AccuracyHistory, AccuracyHistoryAdapter.AccuracyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccuracyViewHolder {
        val binding = ItemAccuracyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccuracyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccuracyViewHolder, position: Int,) {
        val accuracyHistory = getItem(position)

        holder.bind(accuracyHistory)
        holder.binding.imageButtonDelete.setOnClickListener {
            onDeleteClickListener.invoke(accuracyHistory)
        }
    }

    inner class AccuracyViewHolder(val binding: ItemAccuracyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(accuracyHistory: AccuracyHistory) {
            binding.textViewAccuracy.text = "Akurasi:"

            binding.textViewAccuracy.setTextColor(Color.BLACK)

            if (accuracyHistory.accuracy < 50) {
                binding.textViewAccuracy.append(" ${accuracyHistory.accuracy}")
                binding.textViewAccuracy.setTextColor(ContextCompat.getColor(binding.root.context, R.color.yellow))
            } else {
                binding.textViewAccuracy.append(" ${accuracyHistory.accuracy}")
                binding.textViewAccuracy.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
            }

            binding.textViewPredictedAksara.text = "Nama Aksara: ${accuracyHistory.predictedAksara}"
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AccuracyHistory>() {
            override fun areItemsTheSame(oldItem: AccuracyHistory, newItem: AccuracyHistory): Boolean {
                return oldItem.id == newItem.id //
            }

            override fun areContentsTheSame(oldItem: AccuracyHistory, newItem: AccuracyHistory): Boolean {
                return oldItem == newItem
            }
        }
    }
}
