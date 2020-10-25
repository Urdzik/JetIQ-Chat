package com.example.jetiq_chat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.jetiq_chat.databinding.FragmentLoginBinding
import com.example.jetiq_chat.repository.validation.exceptions.EmailValidatorException
import com.example.jetiq_chat.repository.validation.exceptions.PasswordValidatorException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLoginBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

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
    }


}