package com.subhajitrajak.instagramclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.subhajitrajak.instagramclone.databinding.FragmentMyPostsBinding

class MyPosts : Fragment() {
    private lateinit var binding: FragmentMyPostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
    }
}