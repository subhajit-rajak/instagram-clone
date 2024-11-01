package com.subhajitrajak.instagramclone.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.PostItemHomeBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE


class PostAdapter(var context: Context, private var postList: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.MyHolder>() {
    inner class MyHolder(var binding: PostItemHomeBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val binding = PostItemHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        try {
            Firebase.firestore.collection(USER_NODE).document(postList[position].uid).get().addOnSuccessListener {
                val user=it.toObject<User>()
                Glide.with(context).load(user!!.image).placeholder(R.drawable.profile).into(holder.binding.profileImage)
                val userName = user.username
                val caption = postList[position].caption
                holder.binding.name.text=userName
                holder.binding.postCaption.text= Html.fromHtml("<b>$userName</b> $caption")
            }
        } catch (_: Exception) {}

        Glide.with(context).load(postList[position].postUrl).into(holder.binding.postImage)
        holder.binding.time.text= TimeAgo.using(postList[position].time)

        holder.binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, postList[position].postUrl)
            context.startActivity(Intent.createChooser(intent, "Share"))
        }

        val postRef = Firebase.firestore.collection("Post").document(postList[position].postId)
        val userId = Firebase.auth.currentUser!!.uid
        handlePostLikes(holder, postRef, userId)
    }

    private fun handlePostLikes(
        holder: MyHolder,
        postRef: DocumentReference,
        userId: String
    ) {
        var isLiked = false

        postRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Retrieve the current likes map
                val likesMap = documentSnapshot.get("likes") as? Map<String, Boolean> ?: emptyMap()
                isLiked = likesMap.containsKey(userId)
                holder.binding.likeCount.text = likesMap.count { it.value }.toString()

                holder.binding.like.setImageResource(if (isLiked) R.drawable.heart_filled else R.drawable.heart)
            } else {
                isLiked = false
                holder.binding.likeCount.text = R.string._0.toString()
                holder.binding.like.setImageResource(R.drawable.heart)
            }
        }

        holder.binding.like.setOnClickListener {
            if (isLiked) {
                holder.binding.like.setImageResource(R.drawable.heart)
                holder.binding.likeCount.text = holder.binding.likeCount.text.toString().toInt().minus(1).toString()
                postRef.update("likes.$userId", FieldValue.delete())
            } else {
                holder.binding.like.setImageResource(R.drawable.heart_filled)
                holder.binding.likeCount.text = holder.binding.likeCount.text.toString().toInt().plus(1).toString()
                postRef.update("likes.$userId", true)
            }
            isLiked = !isLiked
        }
    }
}