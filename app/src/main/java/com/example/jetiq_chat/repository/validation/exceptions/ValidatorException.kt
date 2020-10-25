package com.example.jetiq_chat.repository.validation.exceptions

import androidx.annotation.StringRes
import org.apache.commons.validator.ValidatorException

open class ValidatorException(@StringRes val errorRes: Int) : ValidatorException()
