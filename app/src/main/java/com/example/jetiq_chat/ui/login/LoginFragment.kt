package com.example.jetiq_chat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.jetiq_chat.databinding.FragmentLoginBinding
import com.example.jetiq_chat.model.UserEntry
import com.example.jetiq_chat.repository.validation.exceptions.EmailValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.PasswordValidatorException
import com.example.jetiq_chat.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    private lateinit var dataStore: DataStore<Preferences>

    lateinit var exampleCounterFlow: Flow<String?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLoginBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        dataStore = requireContext().createDataStore(
            name = "settings"
        )
        exampleCounterFlow = dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preference ->
                preference[Constants.KEY_DATA]
            }

        lifecycleScope.launch {
            exampleCounterFlow.collect {
                it?.let {
                    withContext(Dispatchers.Main){
                        navigateToHome(it)
                    }
                }
            }
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.email.text.toString()
            val pass = binding.pass.text.toString()
            viewModel.login(email, pass)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.error.observe(viewLifecycleOwner, {
            when (it) {
                is PasswordValidatorException -> {
                    binding.pass.setError(getString(it.errorRes), true)
                }

                is EmailValidatorException -> {
                    binding.email.setError(getString(it.errorRes), true)
                }
                else -> {
                    it.printStackTrace()
                }
            }
        })

        viewModel.user.observe(viewLifecycleOwner, {
            it?.let {
                navigateToHome(it.id)
                lifecycleScope.launch {
                    setUser(it.id)
                }
            }
        })
    }



    suspend fun setUser(userId: String) {
        dataStore.edit { preferences ->
            preferences[Constants.KEY_DATA] = userId
        }
    }


    private fun navigateToHome(userId: String) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

}