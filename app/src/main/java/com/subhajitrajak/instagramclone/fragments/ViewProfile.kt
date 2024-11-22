package com.subhajitrajak.instagramclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentViewProfileBinding
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.FOLLOWINGS
import com.subhajitrajak.instagramclone.utils.USER_NODE

class ViewProfile : Fragment() {
    private lateinit var binding: FragmentViewProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        val userId = arguments?.getString("userId")
        Log.e("TAG", "User id: $userId")
        if (isAdded) {
            if (userId != null) fetchUserData(userId)
            else Toast.makeText(requireContext(), "User Not Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUserData(userId: String) {
        Firebase.firestore.collection(USER_NODE).document(userId).get()
            .addOnSuccessListener {
                val user: User = it.toObject<User>()!!
                binding.profileName.text = user.name
                binding.profileUsername.text = user.username
                if (user.bio.isNullOrEmpty()) {
                    binding.profileBio.visibility = View.GONE
                } else {
                    binding.profileBio.text = user.bio
                }

                if (user.website.isNullOrEmpty()) {
                    binding.profileWebsite.visibility = View.GONE
                } else {
                    binding.profileWebsite.text = user.website
                }

                if (!user.image.isNullOrEmpty() && isAdded) {
                    Glide.with(requireContext()).load(user.image).into(binding.profileImage)
                }
                setupFollowBottom(user)
            }

        Firebase.firestore.collection(userId + FOLLOWINGS).get().addOnSuccessListener {
            val followings = it.documents.size
            binding.following.text = followings.toString()
        }

        Firebase.firestore.collection(userId).get().addOnSuccessListener {
            val posts = it.documents.size
            binding.posts.text = posts.toString()
        }
    }

    private fun setupFollowBottom(user: User) {
        var isFollowed = false
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOWINGS)
            .whereEqualTo("email", user.email).get().addOnSuccessListener {
                if (!isAdded) return@addOnSuccessListener

                if (it.documents.size == 0) {
                    isFollowed = false
                } else {
                    binding.followBtn.text = "Following"
                    binding.followBtn.setBackgroundColor(requireContext().getColor(R.color.greyButton))
                    isFollowed = true
                }
            }
        binding.followBtn.setOnClickListener {
            if (isFollowed) {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOWINGS)
                    .whereEqualTo("email", user.email).get().addOnSuccessListener {
                        if (!isAdded) return@addOnSuccessListener

                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOWINGS)
                            .document(it.documents[0].id).delete()
                        binding.followBtn.text = "Follow"
                        binding.followBtn.setBackgroundColor(requireContext().getColor(R.color.button))
                        isFollowed = false
                    }
            } else {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOWINGS)
                    .document().set(user).addOnSuccessListener {
                        if (!isAdded) return@addOnSuccessListener

                        binding.followBtn.text = "Following"
                        binding.followBtn.setBackgroundColor(requireContext().getColor(R.color.greyButton))
                        isFollowed = true
                    }
            }
        }
    }
}