package com.example.jetiq_chat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserEntry(
    val id: String ="",
    val email: String? = ""
): Parcelable