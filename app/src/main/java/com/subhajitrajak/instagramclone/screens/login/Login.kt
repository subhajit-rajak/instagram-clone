package com.subhajitrajak.instagramclone.screens.login

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val text = "<font color=#6B6B6B>Don't have an account? </font> <font color=#00A3FF>Sign up.</font>"
        binding.createNewAccountBtn.text = Html.fromHtml(text)

        binding.createNewAccountBtn.setOnClickListener {
            startActivity(Intent(this@Login, Signup::class.java))
            finish()
        }

        binding.login.setOnClickListener {
            if((binding.email.text.toString() == "") or (binding.password.text.toString() == "")) {
                Toast.makeText(this@Login, "Fill all details", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(binding.email.text.toString(), binding.password.text.toString())
                Firebase.auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this@Login, Home::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@Login, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}