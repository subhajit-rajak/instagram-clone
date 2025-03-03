package com.subhajitrajak.instagramclone.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.StoriesRvDesignBinding
import com.subhajitrajak.instagramclone.models.User

class StoriesAdapter(
    var context: Context,
    private var followingList: ArrayList<User>
): RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    inner class ViewHolder(var binding: StoriesRvDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoriesRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(followingList[position].image).placeholder(R.drawable.profile).into(holder.binding.profilePic)
        holder.binding.username.text = followingList[position].username

        // animation on touched
        holder.binding.imageTab.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.binding.imageTab.startAnimation(scaleAnimation(0.9f, 100))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val scaleUp = scaleAnimation(1f, 200)
                    scaleUp.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            v.performClick()

                            // navigate to stories screen
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                    holder.binding.imageTab.startAnimation(scaleUp)
                }
            }
            true
        }

        /*
        // adding padding to the last element
        if (position == followingList.size - 1) {
            val density = context.resources.displayMetrics.density
            val paddingInPx = (100 * density).toInt() // dp to px conversion
            holder.itemView.setPadding(0,0,paddingInPx,0)
        }
         */
    }

    override fun getItemCount(): Int = followingList.size

    private fun scaleAnimation(toScale: Float, duration: Long): Animation {
        return ScaleAnimation(
            1f, toScale,
            1f, toScale,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            this.duration = duration
            interpolator = OvershootInterpolator()
            fillAfter = true
        }
    }
}