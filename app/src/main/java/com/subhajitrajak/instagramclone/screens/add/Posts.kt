package com.subhajitrajak.instagramclone.screens.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_FOLDER
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.databinding.ActivityPostsBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE

class Posts : AppCompatActivity() {
    private val binding by lazy {
        ActivityPostsBinding.inflate(layoutInflater)
    }
    private var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        binding.loading.text = getString(R.string.uploading)
        uri?.let {
            uploadImage(uri, POST_FOLDER) { url ->
                if (url != null) {
                    imageUrl = url
                    binding.selectImage.setImageURI(uri)
                    binding.loading.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }
        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.shareBtn.setOnClickListener {
            val postId = Firebase.firestore.collection(POST).document().id

            val post = Post(
                postUrl = imageUrl!!,
                caption = binding.caption.text.toString(),
                userId = Firebase.auth.currentUser!!.uid,
                time = System.currentTimeMillis(),
                postId = postId
            ).apply {
                this.postId = postId
            }

            Firebase.firestore.collection(POST).document(postId).set(post)
                .addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                        .document(postId)
                        .set(post)
                        .addOnSuccessListener {
                            startActivity(Intent(this@Posts, Home::class.java))
                            finish()
                        }
                }

        }
    }
}