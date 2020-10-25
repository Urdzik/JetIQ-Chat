package com.example.jetiq_chat.repository.auth


import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.utils.Callback
import org.json.JSONObject

interface AuthRepository {

    suspend fun signUp(email: String, password: String, callback: Callback<UserEntry>)

    suspend fun signIn(emailStr: String, passwordStr: String, callback: Callback<UserEntry>)

    suspend fun logout()

}