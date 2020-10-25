package com.example.jetiq_chat.repository

import androidx.lifecycle.MutableLiveData
import com.example.jetiq_chat.model.ChatMessage
import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.utils.Callback
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference

interface FirebaseManager {

    fun user(): FirebaseUser?

    suspend fun login(email: String, password: String, callback: Callback<UserEntry>)

    suspend fun register(email: String, password: String, callback: Callback<UserEntry>)

    suspend fun logout()

    suspend fun addUserToRoom(roomId: String, callback: Callback<Any?>)

    fun sendChatMessage(message: String, callback: Callback<Any?>)

    fun observeChatMessages()

    fun getMessages(): MutableLiveData<List<ChatMessage>>
}