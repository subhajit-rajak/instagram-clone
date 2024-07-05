package com.subhajitrajak.instagramclone.uploads

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.ActivityReelsBinding

class Reels : AppCompatActivity() {
    private val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}