package com.example.jetiq_chat.dagger.module

import com.example.jetiq_chat.repository.FirebaseManager
import com.example.jetiq_chat.repository.FirebaseManagerImpl
import com.example.jetiq_chat.repository.auth.AuthRepository
import com.example.jetiq_chat.repository.auth.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun getAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository = authRepository


    @Provides
    @Singleton
    fun getFirebaseManager(firebaseManager: FirebaseManagerImpl): FirebaseManager = firebaseManager


}