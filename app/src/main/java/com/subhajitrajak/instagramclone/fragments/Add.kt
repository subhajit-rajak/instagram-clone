package com.subhajitrajak.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.subhajitrajak.instagramclone.databinding.FragmentAddBinding
import com.subhajitrajak.instagramclone.uploads.Posts
import com.subhajitrajak.instagramclone.uploads.Reels


class Add : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddBinding.inflate(inflater, container, false)

        binding.post.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), Posts::class.java))
        }

        binding.reel.setOnClickListener {
            activity?.startActivity(Intent(requireContext(), Reels::class.java))
        }

        return binding.root
    }

    companion object {

    }
}