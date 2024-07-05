package com.subhajitrajak.instagramclone.uploads

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.Models.Reel
import com.subhajitrajak.instagramclone.databinding.ActivityReelsBinding
import com.subhajitrajak.instagramclone.utils.REEL
import com.subhajitrajak.instagramclone.utils.REEL_FOLDER
import com.subhajitrajak.instagramclone.utils.uploadVideo

class Reels : AppCompatActivity() {
    private val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }
    private lateinit var videoUrl: String
    lateinit var progressDialog : ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri->
        uri?.let {
            uploadVideo(uri, REEL_FOLDER, progressDialog) {
                    url->
                if(url!=null) {
                    videoUrl=url
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        binding.selectVideo.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.shareBtn.setOnClickListener {
            val reel:Reel = Reel(videoUrl!!, binding.caption.text.toString())
            Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ REEL).document().set(reel).addOnSuccessListener {
                    startActivity(Intent(this@Reels, Home::class.java))
                    finish()
                }
            }
        }
    }
}