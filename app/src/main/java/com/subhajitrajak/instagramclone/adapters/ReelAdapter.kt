package com.subhajitrajak.instagramclone.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.subhajitrajak.instagramclone.models.Reel
import com.subhajitrajak.instagramclone.models.User
import com.subhajitrajak.instagramclone.R
import com.subhajitrajak.instagramclone.databinding.ReelDgBinding
import com.subhajitrajak.instagramclone.utils.USER_NODE
import androidx.media3.common.MediaItem
import androidx.media3.common.Player


class ReelAdapter(var context: Context, private var reelList: ArrayList<Reel>) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    private var exoPlayer: ExoPlayer? = null

    inner class ViewHolder(var binding: ReelDgBinding) : RecyclerView.ViewHolder(binding.root) {
        val hideHandler = Handler(Looper.getMainLooper())
        val hideRunnable = Runnable { binding.muteButton.visibility = View.GONE }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReelDgBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = User()
        val userId = reelList[position].userId
        if (userId != null) {
            Firebase.firestore.collection(USER_NODE).document(userId).get()
                .addOnSuccessListener {
                    user = it.toObject<User>()!!
                    if (!user.image.isNullOrEmpty()) {
                        Picasso.get().load(user.image).placeholder(R.drawable.profile)
                            .into(holder.binding.profileImage)
                    }
                    holder.binding.usernameReel.text = user.username
                }
        }

        val reel = reelList[position]
        holder.binding.captionReel.text = reel.caption

        releasePlayer()
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(reel.reelUrl)))
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
            holder.binding.playerView.player = this
        }

        var isMuted = false
        var isLongPressPaused = false

        holder.binding.playerView.setOnClickListener {
            if (!isLongPressPaused) {
                isMuted = !isMuted
                exoPlayer?.volume = if (isMuted) 0f else 1f
                holder.binding.muteButton.setImageResource(
                    if (isMuted)
                        R.drawable.volume_off
                    else
                        R.drawable.volume_up
                )
                showMuteButtonTemporarily(holder)
            }
        }

        // Long press to pause the video
        holder.binding.playerView.setOnLongClickListener {
            isLongPressPaused = true
            exoPlayer?.pause()
            true
        }

        // When released, resume video if it was paused by long press
        holder.binding.playerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP && isLongPressPaused) {
                isLongPressPaused = false
                exoPlayer?.play()
            }
            false
        }

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    holder.binding.seekBar.max = exoPlayer?.duration?.toInt() ?: 0
                }
            }
        })

        val updateSeekBar = object : Runnable {
            override fun run() {
                holder.binding.seekBar.progress = exoPlayer?.currentPosition?.toInt() ?: 0
                holder.binding.seekBar.postDelayed(this, 0)
            }
        }
        holder.binding.seekBar.post(updateSeekBar)

        holder.binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    exoPlayer?.seekTo(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun showMuteButtonTemporarily(holder: ViewHolder) {
        holder.binding.muteButton.visibility = View.VISIBLE
        holder.hideHandler.removeCallbacks(holder.hideRunnable)
        holder.hideHandler.postDelayed(holder.hideRunnable, 1000)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        releasePlayer()
        holder.hideHandler.removeCallbacks(holder.hideRunnable)
        holder.binding.seekBar.removeCallbacks(null)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }
}