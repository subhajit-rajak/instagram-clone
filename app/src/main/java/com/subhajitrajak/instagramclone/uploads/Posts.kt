package com.subhajitrajak.instagramclone.uploads

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Models.Post
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.POST_FOLDER
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.databinding.ActivityPostsBinding

class Posts : AppCompatActivity() {
    private val binding by lazy {
        ActivityPostsBinding.inflate(layoutInflater)
    }
    var imageUrl: String?=null
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri->
        uri?.let {
            uploadImage(uri, POST_FOLDER) {
                url->
                if(url!=null) {
                    binding.selectImage.setImageURI(uri)
                    imageUrl=url
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
            val post = Post(imageUrl!!, binding.caption.text.toString())
            Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document().set(post).addOnSuccessListener {
                    finish()
                }
            }
        }
    }
}