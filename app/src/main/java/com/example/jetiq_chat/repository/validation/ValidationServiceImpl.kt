package com.example.jetiq_chat.repository.validation

import com.example.jetiq_chat.R
import com.example.jetiq_chat.repository.validation.exceptions.ConfirmPasswordValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.EmailValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.PasswordValidatorException
import org.apache.commons.validator.routines.EmailValidator

import javax.inject.Inject

class ValidationServiceImpl @Inject constructor() : ValidationService {

    private val emptyValidator: LengthValidator = LengthValidator(LengthValidator.LENGTH_AT_LEAST, 1)

    @Throws(PasswordValidatorException::class)
    private fun validateCommonError(password: String): Boolean {
        if (!emptyValidator.validate(password)) {
            throw PasswordValidatorException(R.string.error_this_field_required)
        }
        val lengthValidator = LengthValidator(
            LengthValidator.LENGTH_AT_LEAST,
            PASSWORD_LENGTH
        )
        if (!lengthValidator.validate(password)) {
            throw PasswordValidatorException(R.string.error_password_length)
        }
        return true
    }

    @Throws(EmailValidatorException::class)
    override fun validateEmail(email: String): Boolean {
        if (!emptyValidator.validate(email)) {
            throw EmailValidatorException(R.string.error_this_field_required)
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw EmailValidatorException(R.string.error_invalid_email)
        }
        return true
    }

    @Throws(PasswordValidatorException::class)
    override fun validateLoginPassword(password: String): Boolean {
        return validateCommonError(password)
    }

    @Throws(ConfirmPasswordValidatorException::class, PasswordValidatorException::class)
    override fun validateNewPasswords(newPassword: String, reNewPassword: String): Boolean {
        if (!emptyValidator.validate(newPassword)) {
            throw PasswordValidatorException(R.string.error_this_field_required)
        }
        if (!emptyValidator.validate(reNewPassword)) {
            throw ConfirmPasswordValidatorException(R.string.error_this_field_required)
        }
        if (newPassword != reNewPassword) {
            throw ConfirmPasswordValidatorException(R.string.error_password_do_not_match)
        }
        return true
    }

    private fun verifyPasswordPattern(password: String): Boolean {
        val pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}"
        return password.matches(pattern.toRegex())
    }

    companion object {

        private const val PASSWORD_LENGTH = 8
    }
}
