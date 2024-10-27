package com.subhajitrajak.instagramclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.Models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.adapters.SearchAdapter
import com.subhajitrajak.instagramclone.databinding.FragmentSearchBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE

class Search : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private var userList = ArrayList<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        Firebase.firestore.collection(USER_NODE).get().addOnSuccessListener {
            val tempList = ArrayList<User>()
            userList.clear()
            for(userObj in it.documents) {
                if(userObj.id == FirebaseAuth.getInstance().currentUser!!.uid) continue

                val user: User = userObj.toObject<User>()!!
                tempList.add(user)
            }
            userList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }

        binding.searchButton.setOnClickListener {
            val text = binding.searchView.text.toString()

            Firebase.firestore.collection(USER_NODE).whereEqualTo("name", text).get().addOnSuccessListener {
                val tempList = ArrayList<User>()
                userList.clear()
                if(!it.isEmpty) {
                    for(userObj in it.documents) {
                        if(userObj.id == FirebaseAuth.getInstance().currentUser!!.uid) continue

                        val user: User = userObj.toObject<User>()!!
                        tempList.add(user)
                    }
                    userList.addAll(tempList)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}