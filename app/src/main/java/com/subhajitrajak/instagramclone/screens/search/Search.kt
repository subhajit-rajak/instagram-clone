package com.subhajitrajak.instagramclone.screens.search

import android.annotation.SuppressLint
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
import com.subhajitrajak.instagramclone.models.User
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.visibility = View.GONE
        binding.shimmerRv.startShimmer()
        binding.shimmerRv.visibility = View.VISIBLE

        binding.rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchAdapter(requireContext(), userList)
        binding.rv.adapter = adapter

        Firebase.firestore.collection(USER_NODE).get()
            .addOnSuccessListener {
                val tempList = ArrayList<User>()
                userList.clear()
                for (userObj in it.documents) {
                    if (userObj.id == FirebaseAuth.getInstance().currentUser!!.uid) continue

                    val user: User = userObj.toObject<User>()!!
                    tempList.add(user)
                }
                userList.addAll(tempList)
                adapter.notifyDataSetChanged()

                binding.shimmerRv.stopShimmer()
                binding.shimmerRv.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            }
            .addOnFailureListener {
                binding.shimmerRv.stopShimmer()
                binding.shimmerRv.visibility = View.GONE
                binding.rv.visibility = View.VISIBLE
            }

        binding.searchButton.setOnClickListener {
            val text = binding.searchView.text.toString()

            Firebase.firestore.collection(USER_NODE).whereEqualTo("name", text).get()
                .addOnSuccessListener {
                    val tempList = ArrayList<User>()
                    userList.clear()
                    if (!it.isEmpty) {
                        for (userObj in it.documents) {
                            if (userObj.id == FirebaseAuth.getInstance().currentUser!!.uid) continue

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