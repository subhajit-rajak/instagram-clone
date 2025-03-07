package com.subhajitrajak.instagramclone.screens.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.databinding.MyPostRvDesignBinding
import com.subhajitrajak.instagramclone.utils.POST_ID
import com.subhajitrajak.instagramclone.utils.USER_ID

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
            bundle.putString(POST_ID, postList[position].postId)
            bundle.putString(USER_ID, postList[position].uid)
            holder.itemView.findNavController().navigate(R.id.viewPost, bundle)
        }
    }
}