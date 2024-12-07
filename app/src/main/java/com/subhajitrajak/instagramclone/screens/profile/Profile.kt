package com.subhajitrajak.instagramclone.screens.profile

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.FragmentProfileBinding
import com.subhajitrajak.instagramclone.utils.FOLLOWINGS
import com.subhajitrajak.instagramclone.utils.USER_NODE

class Profile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        updateView()

        binding.editProfile.setOnClickListener {
            navController.navigate(R.id.action_profile_to_editProfile)
        }
        binding.settings.setOnClickListener {
            navController.navigate(R.id.action_profile_to_settings)
        }

        binding.profileWebsite.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = binding.profileWebsite.text.toString().trim()
            val validUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            intent.data = Uri.parse(validUrl)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPagerAdapter.addFragment(MyPosts(), "Posts")
        viewPagerAdapter.addFragment(MyReels(), "Reels")
        binding.viewPager.adapter=viewPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.tab_item_profile, null)
            tab.customView = tabView
            val icon = tabView.findViewById<ImageView>(R.id.tab_icon)

            when (position) {
                0 -> {
                    icon.setImageResource(R.drawable.my_posts)
                    icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
                }
                1 -> {
                    icon.setImageResource(R.drawable.reels)
                    icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey), PorterDuff.Mode.SRC_IN)
                }
            }
            updateTabIcon(tab, false)
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabIcon(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTabIcon(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
//                updateTabIcon(tab, true)
            }
        })

        return binding.root
    }

    private fun updateView() {
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user:User = it.toObject<User>()!!
                binding.profileName.text=user.name
                binding.profileUsername.text=user.username
                if(user.bio.isNullOrEmpty()) {
                    binding.profileBio.visibility = View.GONE
                } else {
                    binding.profileBio.text = user.bio
                }

                if (user.website.isNullOrEmpty()) {
                    binding.profileWebsite.visibility = View.GONE
                } else {
                    binding.profileWebsite.text = user.website
                }

                if(!user.image.isNullOrEmpty() && isAdded) {
                    Glide.with(requireContext()).load(user.image).into(binding.profileImage)
                }
            }

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid+ FOLLOWINGS).get().addOnSuccessListener {
            val followings = it.documents.size
            binding.following.text = followings.toString()
        }

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            val posts = it.documents.size
            binding.posts.text = posts.toString()
        }
    }

    private fun updateTabIcon(tab: TabLayout.Tab, isSelected: Boolean) {
        val tabView = binding.tabLayout.getTabAt(tab.position)?.customView
        val icon = tabView?.findViewById<ImageView>(R.id.tab_icon)

        if (icon != null) {
            val tintColor = if (isSelected) {
                context?.let { ContextCompat.getColor(it, R.color.black) }
            } else {
                context?.let { ContextCompat.getColor(it, R.color.grey) }
            }

            icon.setColorFilter(tintColor!!, PorterDuff.Mode.SRC_IN)
        }
    }
}