package com.subhajitrajak.instagramclone.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.adapters.ReelAdapter
import com.subhajitrajak.instagramclone.databinding.FragmentReelBinding
import com.subhajitrajak.instagramclone.models.Reel
import com.subhajitrajak.instagramclone.utils.REEL


class Reel : Fragment() {
    private lateinit var binding:FragmentReelBinding
    lateinit var adapter: ReelAdapter
    private var reelList = ArrayList<Reel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReelBinding.inflate(inflater, container, false)

        adapter = ReelAdapter(requireContext(), reelList)
        binding.viewPager.adapter = adapter
        Firebase.firestore.collection(REEL).get().addOnSuccessListener {
            val tempList = arrayListOf<Reel>()
            reelList.clear()
            for (i in it.documents) {
                val reel = i.toObject<Reel>()!!
                tempList.add(reel)
            }
            reelList.addAll(tempList)
            reelList.reverse()
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        adapter.releasePlayer()
    }
}