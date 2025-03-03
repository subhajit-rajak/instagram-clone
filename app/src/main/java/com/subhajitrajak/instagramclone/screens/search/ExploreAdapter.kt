package com.subhajitrajak.instagramclone.screens.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.subhajitrajak.instagramclone.databinding.ExploreRvDesignBinding
import com.subhajitrajak.instagramclone.models.Post

class ExploreAdapter(var context: Context, private var postList: ArrayList<Post>) :
    RecyclerView.Adapter<ExploreAdapter.MyHolder>() {
    inner class MyHolder(var binding: ExploreRvDesignBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = ExploreRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        Glide.with(context).load(postList[position].postUrl).into(holder.binding.postImage)
    }
}