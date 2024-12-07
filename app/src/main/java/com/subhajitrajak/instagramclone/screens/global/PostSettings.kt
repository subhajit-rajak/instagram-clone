package com.subhajitrajak.instagramclone.screens.global

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentPostSettingsBinding
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_ID
import com.subhajitrajak.instagramclone.utils.USER_ID
import kotlin.math.log


class PostSettings : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPostSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString(POST_ID)
        val userId = arguments?.getString(USER_ID)

        binding.deletePost.setOnClickListener {
            if (userId == Firebase.auth.currentUser!!.uid) {
                deletePost(postId!!, userId)
            } else {
                Toast.makeText(
                    requireContext(),
                    "You don't have access to delete this post",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deletePost(postId: String, userId: String) {
        val postRef = Firebase.firestore.collection(POST).document(postId)
        val postInUserRef = Firebase.firestore.collection(userId).document(postId)

        postInUserRef.delete()
            .addOnSuccessListener {
                Log.e("Firestore", "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting document", e)
            }

        postRef.delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_postSettings_to_profile)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Encountered an error while deleting post", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}