package com.subhajitrajak.instagramclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Models.Post
import com.subhajitrajak.instagramclone.Models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.adapters.PostAdapter
import com.subhajitrajak.instagramclone.databinding.FragmentHomeBinding
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.USER_NODE

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PostAdapter
    private var postList = ArrayList<Post>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.firestore.collection(USER_NODE).document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
            val user=it.toObject<User>()
            Glide.with(requireContext()).load(user!!.image).placeholder(R.drawable.profile).into(binding.profilePic)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter= PostAdapter(requireContext(), postList)
        binding.postRv.layoutManager= LinearLayoutManager(requireContext())
        binding.postRv.adapter=adapter
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)

        Firebase.firestore.collection(POST).get().addOnSuccessListener {
            var tempList= arrayListOf<Post>()
            postList.clear()
            for(i in it.documents){
                var post: Post =i.toObject<Post>()!!
                tempList.add(post)
            }
            postList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}