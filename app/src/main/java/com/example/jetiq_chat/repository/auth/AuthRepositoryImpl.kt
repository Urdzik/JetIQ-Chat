package com.example.jetiq_chat.repository.auth

import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.repository.FirebaseManager
import com.example.jetiq_chat.utils.Callback
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseManager
) : AuthRepository {

    override suspend fun signUp(email: String, password: String, callback: Callback<UserEntry>) {
        firebaseManager.register(email, password, object : Callback<UserEntry> {
            override fun onSuccess(body: UserEntry) {
                callback.onSuccess(body)
            }

            override fun onError(reason: String?) {
                callback.onError(reason)
            }
        })
    }


    override suspend fun signIn(
        emailStr: String,
        passwordStr: String,
        callback: Callback<UserEntry>
    ) {
        firebaseManager.login(emailStr, passwordStr, object : Callback<UserEntry> {
            override fun onSuccess(body: UserEntry) {
                callback.onSuccess(body)
            }

            override fun onError(reason: String?) {
                callback.onError(reason)
            }
        })
    }


    override suspend fun logout() {
        firebaseManager.logout()
    }

}