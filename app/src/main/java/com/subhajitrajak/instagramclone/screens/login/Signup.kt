package com.subhajitrajak.instagramclone.screens.login

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Home
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.ActivitySignupBinding
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.USER_NODE
import com.subhajitrajak.instagramclone.utils.USER_PROFILE_FOLDER
import com.subhajitrajak.instagramclone.utils.isUsernameValid
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.utils.usernameAlreadyExists


class Signup : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it != null) {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
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

        val text =
            "<font color=#6B6B6B>Already have an account? </font> <font color=#00A3FF>Log in.</font>"
        binding.loginBtn.text = Html.fromHtml(text)
        user = User()

        binding.registerBtn.setOnClickListener {
            val username = binding.usernameSignUp.text.toString()
            val email = binding.email.text.toString()
            val password = binding.passwordSignup.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if ((username == "") or
                (email == "") or
                (password == "") or
                (confirmPassword == "")
            ) {
                Toast.makeText(this@Signup, "Fill all required fields", Toast.LENGTH_SHORT)
                    .show()
            } else if (password != confirmPassword
            ) {
                Toast.makeText(this@Signup, "Passwords aren't matching", Toast.LENGTH_SHORT)
                    .show()
            } else if (!isUsernameValid(username)) {
                Toast.makeText(
                    this,
                    "the username can only use letters, numbers, underscores and periods",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                usernameAlreadyExists(username) { exists ->
                    if (exists) {
                        Toast.makeText(
                            this,
                            "The username $username is not available",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            email,
                            password
                        ).addOnCompleteListener { result ->

                            if (result.isSuccessful) {
                                user.username = username
                                user.email = email
                                user.password = password
                                user.userId = Firebase.auth.currentUser!!.uid
                                Firebase.firestore.collection(USER_NODE)
                                    .document(Firebase.auth.currentUser!!.uid).set(user)
                                    .addOnSuccessListener {
                                        startActivity(Intent(this@Signup, Home::class.java))
                                        finish()
                                    }

                                Toast.makeText(
                                    this@Signup,
                                    "Registered Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                Toast.makeText(
                                    this@Signup,
                                    result.exception?.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@Signup, Login::class.java))
            finish()
        }
    }
}