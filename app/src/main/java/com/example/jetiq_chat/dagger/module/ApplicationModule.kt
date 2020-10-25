package com.example.jetiq_chat.dagger.module

import com.example.jetiq_chat.repository.validation.ValidationService
import com.example.jetiq_chat.repository.validation.ValidationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object ApplicationModule{

    @Provides
    @Singleton
    fun getValidationService(validation: ValidationServiceImpl): ValidationService = validation
}