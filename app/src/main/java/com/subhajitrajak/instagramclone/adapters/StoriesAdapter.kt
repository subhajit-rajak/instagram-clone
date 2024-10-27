package com.subhajitrajak.instagramclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.StoriesRvDesignBinding
import com.subhajitrajak.instagramclone.utils.FOLLOWINGS

class StoriesAdapter(
    var context: Context,
    private var followingList: ArrayList<User>
): RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: StoriesRvDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoriesRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(followingList[position].image).placeholder(R.drawable.profile).into(holder.binding.profilePic)
        holder.binding.username.text = followingList[position].name
    }

    override fun getItemCount(): Int = followingList.size
}