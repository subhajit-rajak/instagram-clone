package com.subhajitrajak.instagramclone.screens.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.SearchRvDesignBinding
import com.subhajitrajak.instagramclone.utils.FOLLOWINGS

class SearchAdapter(
    var context: Context,
    private var userList: ArrayList<User>
): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: SearchRvDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(userList[position].image).placeholder(R.drawable.profile).into(holder.binding.profileImage)
        holder.binding.username.text = userList[position].username
        holder.binding.name.text = userList[position].name

        var isFollowed = false
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOWINGS).whereEqualTo("email", userList[position].email).get().addOnSuccessListener {
            if(it.documents.size == 0) {
                isFollowed = false
            } else {
                holder.binding.follow.text = "Following"
                holder.binding.follow.setBackgroundColor(context.resources.getColor(R.color.greyButton))
                isFollowed = true
            }
        }
        holder.binding.follow.setOnClickListener {
            if(isFollowed) {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOWINGS).whereEqualTo("email", userList[position].email).get().addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOWINGS).document(it.documents[0].id).delete()
                    holder.binding.follow.text = "Follow"
                    holder.binding.follow.setBackgroundColor(context.resources.getColor(R.color.button))
                    isFollowed = false
                }
            } else {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOWINGS).document().set(userList[position])
                holder.binding.follow.text = "Following"
                holder.binding.follow.setBackgroundColor(context.resources.getColor(R.color.greyButton))
                isFollowed = true
            }
        }

        holder.binding.profileCard.setOnClickListener {
            val bundle = Bundle()
            val userId = userList[position].userId
            bundle.putString("userId", userId)
            holder.itemView.findNavController().navigate(R.id.action_search_to_viewProfile, bundle)
            Log.e("TAG", "User id: $userId")
        }
    }
}