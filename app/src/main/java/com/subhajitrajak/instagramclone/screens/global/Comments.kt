package com.subhajitrajak.instagramclone.screens.global

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.databinding.FragmentCommentsBinding
import com.subhajitrajak.instagramclone.models.Comment
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.COMMENTS
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_ID
import com.subhajitrajak.instagramclone.utils.TYPE
import com.subhajitrajak.instagramclone.utils.USER_ID
import com.subhajitrajak.instagramclone.utils.USER_NODE

class Comments : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCommentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(TYPE)
        val postId = arguments?.getString(POST_ID)
        val userId = arguments?.getString(USER_ID)
        Log.e("TAG", "Post id: $postId")
        Log.e("TAG", "User id: $userId")
        if (postId != null && userId!=null && type!=null) fetchComments(postId, userId, type)

        binding.postComment.setOnClickListener {
            val commentText = binding.addComment.text.toString()
            if (commentText.isNotEmpty()) {
                val comment = Comment(Firebase.auth.currentUser!!.uid, commentText, System.currentTimeMillis())
                val postRef = Firebase.firestore.collection(type!!).document(postId!!)
                postRef.update(COMMENTS, FieldValue.arrayUnion(comment)).addOnSuccessListener {
                    binding.addComment.text.clear()
                }
            }
        }

        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user: User = it.toObject<User>()!!
                if(!user.image.isNullOrEmpty() && isAdded) {
                    Glide.with(requireContext()).load(user.image).into(binding.profileImage)
                }
            }
    }

    private fun fetchComments(postId: String, userId: String, type: String) {
        val postRef = Firebase.firestore.collection(type).document(postId)
        postRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val commentsArray = documentSnapshot.get(COMMENTS) as? List<Map<String, Any>>
                val comments = commentsArray?.map { commentMap ->
                    Comment(
                        userId = commentMap["userId"] as String,
                        text = commentMap["text"] as String,
                        time = commentMap["time"] as Long
                    )
                } ?: emptyList()
                if (comments.isNotEmpty()) {
                    val navController = findNavController()
                    val adapter = CommentsAdapter(navController, requireContext(), comments, userId)
                    binding.rv.adapter = adapter
                    binding.rv.layoutManager = LinearLayoutManager(requireContext())
                    binding.rv.scrollToPosition(comments.size - 1)
                } else {
                    binding.nothingToShow.visibility = View.VISIBLE
                    binding.rv.visibility = View.GONE
                }
            }
        }
    }
}