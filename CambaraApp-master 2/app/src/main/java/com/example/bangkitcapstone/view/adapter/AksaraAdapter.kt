package com.example.bangkitcapstone.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.databinding.ItemAksaraBinding
import com.example.bangkitcapstone.view.camera.CameraActivity

class AksaraAdapter(private val context: Context) :
    ListAdapter<AksaraItem, AksaraAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAksaraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val aksara = getItem(position)
        holder.bind(aksara)
        holder.itemView.setOnClickListener {
            val moveStoryDataIntent = Intent(holder.itemView.context, CameraActivity::class.java)
            moveStoryDataIntent.putExtra(CameraActivity.ID, aksara.id)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.binding.ivItemPhoto, "aksaraImage")
                )
            context.startActivity(moveStoryDataIntent, optionsCompat.toBundle())


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
