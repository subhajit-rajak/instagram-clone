package com.subhajitrajak.instagramclone.uploads

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.Models.Post
import com.subhajitrajak.instagramclone.Models.User
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_FOLDER
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.databinding.ActivityPostsBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Posts : AppCompatActivity() {
    private val binding by lazy {
        ActivityPostsBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, POST_FOLDER) { url ->
                if (url != null) {
                    binding.selectImage.setImageURI(uri)
                    imageUrl = url
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
            startActivity(Intent(this@Posts, Home::class.java))
            finish()
        }
        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.shareBtn.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document().get().addOnSuccessListener {
                val user = it.toObject<User>()
                val post = Post(
                    postUrl = imageUrl!!,
                    caption = binding.caption.text.toString(),
                    name = Firebase.auth.currentUser!!.uid,
                    time = System.currentTimeMillis()
                )
                Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                        .set(post).addOnSuccessListener {
                        startActivity(Intent(this@Posts, Home::class.java))
                        finish()
                    }
                }
            }
        }
    }
}