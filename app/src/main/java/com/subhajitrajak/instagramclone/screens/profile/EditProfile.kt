package com.subhajitrajak.instagramclone.screens.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentEditProfileBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE
import com.subhajitrajak.instagramclone.utils.USER_PROFILE_FOLDER
import com.subhajitrajak.instagramclone.utils.isUsernameValid
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.utils.usernameAlreadyExists

class EditProfile : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it != null) {
                    user.image = it
                    binding.editProfilePhoto.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = User()

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                user = it.toObject<User>()!!
                if (!user.image.isNullOrEmpty()) {
                    Picasso.get().load(user.image).placeholder(R.drawable.profile)
                        .into(binding.editProfilePhoto)
                }
                binding.editName.setText(user.name)
                binding.editUsername.setText(user.username)
                binding.editWebsite.setText(user.website)
                binding.editBio.setText(user.bio)
                binding.editEmail.setText(user.email)
                binding.editPhone.setText(user.phone)
                binding.editGender.setText(user.gender)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        binding.changeProfilePhoto.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.saveBtn.setOnClickListener {
            val username = binding.editUsername.text.toString()
            if (!isUsernameValid(username)) {
                Toast.makeText(
                    requireContext(),
                    "the username can only use letters, numbers, underscores and periods",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                usernameAlreadyExists(username) { exists ->
                    if (exists && username != user.username) {
                        Toast.makeText(
                            requireContext(),
                            "The username $username is not available",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        user.name = binding.editName.text.toString()
                        user.username = username
                        user.website = binding.editWebsite.text.toString()
                        user.bio = binding.editBio.text.toString()
                        user.email = binding.editEmail.text.toString()
                        user.phone = binding.editPhone.text.toString()
                        user.gender = binding.editGender.text.toString()

                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid)
                            .set(user).addOnSuccessListener {
                                findNavController().popBackStack()
                                Toast.makeText(
                                    requireContext(),
                                    "Saved Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Couldn't save",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
    }
}