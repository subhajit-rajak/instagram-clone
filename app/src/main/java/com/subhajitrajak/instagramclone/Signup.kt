package com.subhajitrajak.instagramclone

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
import com.squareup.picasso.Picasso
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.utils.USER_NODE
import com.subhajitrajak.instagramclone.utils.USER_PROFILE_FOLDER
import com.subhajitrajak.instagramclone.utils.uploadImage
import com.subhajitrajak.instagramclone.databinding.ActivitySignupBinding


class Signup : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }
    private lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if(it!=null) {
                    user.image=it
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

        val text = "<font color=#6B6B6B>Already have an account? </font> <font color=#00A3FF>Log in.</font>"
        binding.loginBtn.text = Html.fromHtml(text)
        user = User()

        if (intent.hasExtra("MODE")) {
            if(intent.getIntExtra("MODE", -1)==1) {
                binding.registerBtn.text="Update Profile"
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        user = it.toObject<User>()!!
                        if(!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }
                        binding.usernameSignUp.setText(user.name)
                        binding.email.setText(user.email)
                        binding.passwordSignup.setText(user.password)
                        binding.confirmPassword.setText(user.password)
                    }
            }
        }

        binding.registerBtn.setOnClickListener {
            if (intent.hasExtra("MODE")) {
                if(intent.getIntExtra("MODE", -1)==1) {
                    Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this@Signup, Home::class.java))
                            finish()
                        }
                }
            } else {
                if ((binding.usernameSignUp.text.toString() == "") or
                    (binding.email.text.toString() == "") or
                    (binding.passwordSignup.text.toString() == "") or
                    (binding.confirmPassword.text.toString() == "")
                ) {
                    Toast.makeText(this@Signup, "Fill all required fields", Toast.LENGTH_SHORT).show()
                } else if (binding.passwordSignup.text.toString() != binding.confirmPassword.text.toString()
                ) {
                    Toast.makeText(this@Signup, "Passwords aren't matching", Toast.LENGTH_SHORT).show()
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.email.text.toString(),
                        binding.passwordSignup.text.toString()
                    ).addOnCompleteListener { result ->

                        if (result.isSuccessful) {
                            user.name = binding.usernameSignUp.text.toString()
                            user.email = binding.email.text.toString()
                            user.password = binding.passwordSignup.text.toString()
                            user.userId = Firebase.auth.currentUser!!.uid
                            Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid).set(user)
                                .addOnSuccessListener {
                                    startActivity(Intent(this@Signup, Home::class.java))
                                    finish()
                                }

                            Toast.makeText(this@Signup, "Registered Succesfully", Toast.LENGTH_SHORT)
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
        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@Signup, Login::class.java))
            finish()
        }
    }
}