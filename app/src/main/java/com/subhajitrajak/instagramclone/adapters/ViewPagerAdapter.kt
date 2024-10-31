package com.subhajitrajak.instagramclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val fragmentList = mutableListOf<Fragment>()
    private val titleList = mutableListOf<String>()
//    override fun getCount(): Int {
//        return fragmentList.size
//    }

//    override fun getItem(position: Int): Fragment {
//        return fragmentList[position]
//    }

    //    override fun getPageTitle(position: Int): CharSequence? {
//        return titleList[position]
//    }
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
        notifyItemInserted(fragmentList.size-1)
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}