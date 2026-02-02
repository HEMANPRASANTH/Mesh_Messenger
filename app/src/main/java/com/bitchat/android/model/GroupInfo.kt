package com.bitchat.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupInfo(
    val id: String,
    val name: String,
    val region: String,
    val creatorPeerID: String,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable
