package com.example.bangkitcapstone.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.databinding.ItemAksaraBinding
import java.util.*

class AksaraAdapter(private val context: Context) :
    ListAdapter<AksaraItem, AksaraAdapter.MyViewHolder>(DIFF_CALLBACK), Filterable {

    private var aksaraListFull: List<AksaraItem> = ArrayList()

    init {
        aksaraListFull = currentList.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemAksaraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val aksara = getItem(position)
        holder.bind(aksara)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<AksaraItem>()

                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(aksaraListFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()

                    for (aksara in aksaraListFull) {
                        if (aksara.name.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                            filteredList.add(aksara)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<AksaraItem>)
            }
        }
    }

    class MyViewHolder(val binding: ItemAksaraBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(aksara: AksaraItem) {
            binding.tvItemName.text = aksara.name
            Glide.with(binding.root.context)
                .load(aksara.urlImage)
                .into(binding.ivItemPhoto)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AksaraItem>() {
            override fun areItemsTheSame(oldItem: AksaraItem, newItem: AksaraItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: AksaraItem,
                newItem: AksaraItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
