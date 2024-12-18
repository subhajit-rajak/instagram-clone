package com.subhajitrajak.instagramclone.screens.add

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.models.Reel
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.databinding.ActivityReelsBinding
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.REEL
import com.subhajitrajak.instagramclone.utils.REEL_FOLDER
import com.subhajitrajak.instagramclone.utils.USER_NODE
import com.subhajitrajak.instagramclone.utils.uploadVideo

class Reels : AppCompatActivity() {
    private val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }
    private lateinit var videoUrl: String
    private lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadVideo(uri, REEL_FOLDER, progressDialog) { url ->
                if (url != null) {
                    videoUrl = url
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

        progressDialog = ProgressDialog(this)

        binding.selectVideo.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.shareBtn.setOnClickListener {
            val userId = Firebase.auth.currentUser!!.uid
            val reelId = Firebase.firestore.collection(REEL).document().id

            val reel = Reel(
                reelUrl = videoUrl,
                caption = binding.caption.text.toString(),
                userId = userId,
                time = System.currentTimeMillis(),
                reelId = reelId
            ).apply {
                this.reelId = reelId
            }
            Firebase.firestore.collection(REEL)
                .document(reelId)
                .set(reel)
                .addOnSuccessListener {
                    Firebase.firestore.collection(userId + REEL)
                        .document(reelId)
                        .set(reel)
                        .addOnSuccessListener {
                            startActivity(Intent(this@Reels, Home::class.java))
                            finish()
                        }
                }

        }
    }
}