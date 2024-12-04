package com.subhajitrajak.instagramclone.models

data class Comment(
    var userId: String = "",
    var text: String = "",
    var time: Long = 0L
)