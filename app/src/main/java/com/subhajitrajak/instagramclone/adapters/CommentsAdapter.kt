package com.subhajitrajak.instagramclone.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.CommentsRvDesignBinding
import com.subhajitrajak.instagramclone.models.Comment
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.USER_NODE

class CommentsAdapter(
    private val navController: NavController,
    var context: Context,
    private var comments: List<Comment>,
    private var postUserId: String
) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: CommentsRvDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommentsRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        val userId = comment.userId

        Firebase.firestore.collection(USER_NODE).document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                if (user != null) {
                    holder.binding.username.text = user.username
                    if (!user.image.isNullOrEmpty()) {
                        Glide.with(context).load(user.image).into(holder.binding.profileImage)
                    }
                }
            }

        holder.binding.author.visibility = if (userId == postUserId) View.VISIBLE else View.GONE
        holder.binding.comment.text = comment.text
        Log.e("comments", "onBindViewHolder: ${comment.userId} and ${comment.text}")
        holder.binding.time.text = TimeAgo.using(comment.time)


        val bundle = Bundle()
        bundle.putString("userId", userId)

        holder.binding.profileImage.setOnClickListener {
            if (userId != Firebase.auth.currentUser!!.uid) {
                navController.navigate(R.id.action_comments_to_viewProfile, bundle)
            } else {
                navController.navigate(R.id.action_comments_to_profile)
            }
        }
    }
}