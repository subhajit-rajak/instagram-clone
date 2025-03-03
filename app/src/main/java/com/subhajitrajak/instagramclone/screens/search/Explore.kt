package com.subhajitrajak.instagramclone.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentExploreBinding
import com.subhajitrajak.instagramclone.models.Post
import com.subhajitrajak.instagramclone.utils.POST
import com.subhajitrajak.instagramclone.utils.SpannedGridLayoutManager

class Explore : Fragment() {
    private lateinit var binding: FragmentExploreBinding
    private lateinit var navController: NavController
    private lateinit var adapter: ExploreAdapter
    private var postList = ArrayList<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.searchView.setOnClickListener {
            navController.navigate(R.id.action_explore_to_search)
        }

        setupView()
    }

    private fun setupView() {
        setupSpannedGridLayout()

        Firebase.firestore.collection(POST)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val tempList= arrayListOf<Post>()
                postList.clear()
                for(i in it.documents){
                    val post: Post =i.toObject<Post>()!!
                    tempList.add(post)
                }
                tempList.shuffle()
                postList.addAll(tempList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Couldn't refresh feed", Toast.LENGTH_SHORT).show()
            }
    }
    private fun setupSpannedGridLayout() {
        val manager = SpannedGridLayoutManager(
            object : SpannedGridLayoutManager.GridSpanLookup {
                override fun getSpanInfo(position: Int): SpannedGridLayoutManager.SpanInfo {
                    // Conditions for 2x2 items
                    return if (position % 6 == 0 || position % 6 == 4) {
                        SpannedGridLayoutManager.SpanInfo(2, 2)
                    } else {
                        SpannedGridLayoutManager.SpanInfo(1, 1)
                    }
                }
            },
            3,  // number of columns
            1f // how big is default item
        )

        binding.exploreRV.layoutManager = manager
        adapter = ExploreAdapter(requireContext(), postList)
        binding.exploreRV.adapter = adapter
    }
}