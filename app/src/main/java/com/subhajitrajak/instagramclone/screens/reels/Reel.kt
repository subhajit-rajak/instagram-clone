package com.subhajitrajak.instagramclone.screens.reels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.databinding.FragmentReelBinding
import com.subhajitrajak.instagramclone.models.Reel
import com.subhajitrajak.instagramclone.utils.REEL


class Reel : Fragment() {
    private lateinit var binding:FragmentReelBinding
    lateinit var adapter: ReelAdapter
    private lateinit var exoPlayer: ExoPlayer
    private var reelList = ArrayList<Reel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReelBinding.inflate(inflater, container, false)

        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        adapter = ReelAdapter(requireContext(), reelList, exoPlayer)
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

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.release()
    }
}