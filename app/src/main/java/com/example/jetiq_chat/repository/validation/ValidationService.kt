package com.example.jetiq_chat.repository.validation

import com.example.jetiq_chat.repository.validation.exceptions.ConfirmPasswordValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.EmailValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.PasswordValidatorException

/** Validation service.  */
interface ValidationService {

    @Throws(EmailValidatorException::class)
    fun validateEmail(email: String): Boolean

    @Throws(PasswordValidatorException::class)
    fun validateLoginPassword(password: String): Boolean

    @Throws(ConfirmPasswordValidatorException::class, PasswordValidatorException::class)
    fun validateNewPasswords(newPassword: String, reNewPassword: String): Boolean


}
