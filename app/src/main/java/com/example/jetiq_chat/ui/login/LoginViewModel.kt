package com.example.jetiq_chat.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.repository.auth.AuthRepository
import com.example.jetiq_chat.repository.validation.ValidationService
import com.example.jetiq_chat.utils.Callback
import com.example.jetiq_chat.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val validationService: ValidationService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _error = MutableLiveData<Exception>()
    val error: LiveData<Exception>
        get() = _error

    private val _user = MutableLiveData<UserEntry>()
    val user: LiveData<UserEntry> get() = _user



    fun login(email: String, pass: String) {
        try {
            if (validationService.validateEmail(email) && validationService.validateLoginPassword(pass)) {
                viewModelScope.launch(Dispatchers.IO) {
                    authRepository.signIn(email, pass, object : Callback<UserEntry> {
                        override fun onSuccess(body: UserEntry) {
                            _user.postValue(body)
                        }

                        override fun onError(reason: String?) {
                            when (reason) {
                                Constants.ERROR_USER_NOT_FOUND -> {
                                    signUp(email, pass)
                                }

//                                Constants.ERROR_NETWORK -> {
//                                    getView()?.hideProgress()
//                                    getView()?.showMessage(R.string.error_network)
//                                }
//
//                                Constants.ERROR_WRONG_PASSWORD -> {
//                                    getView()?.hideProgress()
//                                    getView()?.showPasswordError(R.string.error_wrong_password)
//                                }
//
//                                else -> {
//                                    getView()?.hideProgress()
//                                    getView()?.showMessage(R.string.error_general)
//                                }
                            }
                        }

                    })

                }
            }
        } catch (e: Exception) {
            _error.value = e
        }
    }

    private fun signUp(emailStr: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.signUp(emailStr, pass, object : Callback<UserEntry> {
                override fun onSuccess(body: UserEntry) {
                    _user.postValue(body)
                }

                override fun onError(reason: String?) {
                    when (reason) {
//                        Constants.ERROR_NETWORK -> {
//                            getView()?.showMessage(R.string.error_network)
//                        }
//                        else -> {
//                            getView()?.showMessage(R.string.error_general)
//                        }
                    }
                }
            })

        }
    }
}