package com.subhajitrajak.instagramclone.screens.global

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentViewPostBinding
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_ID
import com.subhajitrajak.instagramclone.utils.USER_ID
import com.subhajitrajak.instagramclone.utils.USER_NODE

class ViewPost : Fragment() {
    private lateinit var binding: FragmentViewPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val postId = arguments?.getString(POST_ID)
        val userId = arguments?.getString(USER_ID)
        Log.e("TAG", "Post id: $postId")
        if (postId != null) fetchUserData(postId)
        else Toast.makeText(requireContext(), "Post Not Found", Toast.LENGTH_SHORT).show()

        binding.moreSettings.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(POST_ID, postId)
            bundle.putString(USER_ID, userId)
            findNavController().navigate(R.id.action_viewPost_to_postSettings, bundle)
        }
    }

    private fun fetchUserData(postId: String) {
        val postRef = Firebase.firestore.collection(POST).document(postId)

        postRef.get().addOnSuccessListener { documentSnapshot ->
            if (isAdded && documentSnapshot.exists()) {
                val post = documentSnapshot.toObject<Post>()
                if (post!=null){
                    loadUserData(post.uid, post.caption)

                    context?.let {
                        Glide.with(it).load(post.postUrl).into(binding.postImage)
                    }
                    binding.time.text = TimeAgo.using(post.time)

                    handleShareIntent(post.postUrl)
                    handlePostLikes(postRef, post.uid)
                }
            }
        }
    }

    private fun handleShareIntent(postUrl: String) {
        binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, postUrl)
            startActivity(Intent.createChooser(intent, "Share"))
        }
    }

    private fun loadUserData(uid: String, caption: String) {
        Firebase.firestore.collection(USER_NODE).document(uid).get().addOnSuccessListener { userDocument ->
            if (isAdded) {
                val user = userDocument.toObject<User>()
                if (user!=null) {
                    context?.let {
                        Glide.with(it).load(user.image).into(binding.profileImage)
                    }
                    val userName = user.username
                    binding.name.text = userName
                    binding.postCaption.text = Html.fromHtml("<b>${user.username}</b> $caption")
                }
            }
        }
    }

    private fun handlePostLikes(
        postRef: DocumentReference,
        userId: String
    ) {
        var isLiked = false

        postRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val likesMap = documentSnapshot.get("likes") as? Map<String, Boolean> ?: emptyMap()
                isLiked = likesMap.containsKey(userId)
                binding.likeCount.text = likesMap.count { it.value }.toString()

                binding.like.setImageResource(if (isLiked) R.drawable.heart_filled else R.drawable.heart)
            } else {
                isLiked = false
                binding.likeCount.text = R.string._0.toString()
                binding.like.setImageResource(R.drawable.heart)
            }
        }

        binding.like.setOnClickListener {
            if (isLiked) {
                binding.like.setImageResource(R.drawable.heart)
                binding.likeCount.text =
                    binding.likeCount.text.toString().toInt().minus(1).toString()
                postRef.update("likes.$userId", FieldValue.delete())
            } else {
                binding.like.setImageResource(R.drawable.heart_filled)
                binding.likeCount.text =
                    binding.likeCount.text.toString().toInt().plus(1).toString()
                postRef.update("likes.$userId", true)
            }
            isLiked = !isLiked
        }
    }
}