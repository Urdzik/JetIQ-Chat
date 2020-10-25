package com.example.jetiq_chat.repository

import androidx.lifecycle.MutableLiveData
import com.example.jetiq_chat.model.ChatMessage
import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.utils.Callback
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.inject.Inject

class FirebaseManagerImpl @Inject constructor(): FirebaseManager {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var roomId = ""
    private val chatMessagesLiveData = MutableLiveData<List<ChatMessage>>()

    override fun user(): FirebaseUser? = auth.currentUser

    override fun getMessages() = chatMessagesLiveData

    override suspend fun login(email: String, password: String, callback: Callback<UserEntry>) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            when {
                it.isSuccessful -> FirebaseAuth.getInstance().currentUser?.let { user ->
                    getUserEntry(user, object : Callback<UserEntry> {
                        override fun onSuccess(body: UserEntry) {
                            callback.onSuccess(body)
                        }

                        override fun onError(reason: String?) {
                            callback.onError(reason)
                        }
                    })

                } ?: callback.onError(it.exception?.message)

                it.exception is FirebaseAuthException -> callback.onError((it.exception as FirebaseAuthException).errorCode)

                it.exception is FirebaseNetworkException -> callback.onError()

                else -> callback.onError(it.exception?.message)
            }
        }
    }

    private fun getUserEntry(user: FirebaseUser, callback: Callback<UserEntry>) {
        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { data ->
                val entry = data.toObject(UserEntry::class.java)
                entry?.let {
                    callback.onSuccess(it)
                } ?: kotlin.run {
                    callback.onError("user is null")
                }
            }.addOnFailureListener {
                callback.onError(it.message)
            }

    }

    private fun setUserEntry(user: FirebaseUser, callback: Callback<UserEntry>) {
        val user =  UserEntry(
            id = user.uid,
            email = user.email
        )
        firestore.collection("users").document(user.id)
            .set(user)
            .addOnSuccessListener { data ->
                data?.let {
                    callback.onSuccess(user)
                } ?: kotlin.run {
                    callback.onError("user is null")
                }
            }.addOnFailureListener {
                callback.onError(it.message)
            }

    }

    override suspend fun register(email: String, password: String, callback: Callback<UserEntry>) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            when {
                it.isSuccessful -> FirebaseAuth.getInstance().currentUser?.let { user ->
                    setUserEntry(user, object : Callback<UserEntry> {
                        override fun onSuccess(body: UserEntry) {
                            callback.onSuccess(body)
                        }

                        override fun onError(reason: String?) {
                            callback.onError(reason)
                        }
                    })

                } ?: callback.onError(it.exception?.message)

                it.exception is FirebaseAuthException -> callback.onError((it.exception as FirebaseAuthException).errorCode)

                it.exception is FirebaseNetworkException -> callback.onError()

                else -> callback.onError(it.exception?.message)
            }
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun addUserToRoom(roomId: String, callback: Callback<Any?>) {

        firestore.collection("rooms")
            .document(roomId)
            .collection("users")
            .add(
                mapOf(
                    Pair("id", user()!!.uid),
                    Pair("name", user()!!.displayName),
                    Pair("image", user()!!.photoUrl)
                )
            )

    }

    override fun sendChatMessage(message: String, callback: Callback<Any?>) {

        firestore.collection("rooms")
            .document(roomId)
            .collection("messages")
            .add(
                mapOf(
                    Pair("text", message),
                    Pair("user", user()!!.uid),
                    Pair("timestamp", Timestamp.now())
                )
            )
    }

    override fun observeChatMessages() {

        firestore.collection("rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { messagesSnapshot, exception ->

                if (exception != null) {
                    exception.printStackTrace()
                    return@addSnapshotListener
                }

                val messages = messagesSnapshot?.documents?.map {
                    ChatMessage(
                        it["text"] as String,
                        it["user"] as String,
                        (it["timestamp"]) as Date
                    )
                }

                messages?.let { chatMessagesLiveData.postValue(messages) }
            }
    }


}