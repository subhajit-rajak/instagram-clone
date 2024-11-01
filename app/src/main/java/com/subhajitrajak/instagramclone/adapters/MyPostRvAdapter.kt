package com.subhajitrajak.instagramclone.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.databinding.MyPostRvDesignBinding
import com.subhajitrajak.instagramclone.fragments.MyPostsDirections
import com.subhajitrajak.instagramclone.fragments.ViewPost

class MyPostRvAdapter(var context: Context, private var postList: ArrayList<Post>) :
    RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: MyPostRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(postList[position].postUrl).into(holder.binding.postCanvas)

        holder.binding.postCanvas.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("postId", postList[position].postId)
            holder.itemView.findNavController().navigate(R.id.viewPost, bundle)
        }
    }
}