package com.example.chatapp.a_authentication.uiAuth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentRegisterUserBinding
import com.example.chatapp.a_authentication.errorHandler.AuthError
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthState
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModel
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModelFactory
import com.example.chatapp.a_application.appContainer
import com.example.chatapp.a_authentication.errorHandler.AuthErrorToString.toMessageRes
import kotlinx.coroutines.launch


class RegisterUser : Fragment() {

    // تعامل مع authError
    private fun showFieldError(
        textView: TextView,
        message: String
    ) {
        textView.text = message
        textView.isVisible = true
    }

    private fun hideFieldError(
        textView: TextView,
    ) {
        textView.text = ""
        textView.isVisible = false
    }

    private fun clearFieldErrors() {
        hideFieldError(binding.tvNameRegisterErrorId)
        hideFieldError(binding.tvEmailRegisterErrorId)
        hideFieldError(binding.tvPasswordRegisterErrorId)
    }

    private fun showGeneralError(message: String) {
        binding.cardLayoutErrorRegisterId.isVisible = true
        binding.tvShowGeneralErrorRegisterId.text = message
    }

    private fun hideGeneralError() {
        binding.cardLayoutErrorRegisterId.isVisible = false
    }

    private fun showAuthError(error: AuthError) {
        clearFieldErrors()
        when (error) {
            AuthError.EmptyName -> {
                showFieldError(
                    binding.tvNameRegisterErrorId,
                    getString(error.toMessageRes())
                )
            }

            AuthError.EmptyEmail -> {
                showFieldError(
                    binding.tvEmailRegisterErrorId,
                    getString(error.toMessageRes())
                )
            }

            AuthError.InvalidEmail -> {
                showFieldError(
                    binding.tvEmailRegisterErrorId,
                    getString(error.toMessageRes())
                )
            }

            AuthError.EmptyPassword -> {
                showFieldError(
                    binding.tvPasswordRegisterErrorId,
                    getString(error.toMessageRes())
                )
            }

            AuthError.WeakPassword -> {
                showFieldError(
                    binding.tvPasswordRegisterErrorId,
                    getString(error.toMessageRes())
                )
            }

            AuthError.InvalidCredentials -> {
                showGeneralError(
                    getString(error.toMessageRes())
                )
            }

            AuthError.NetworkError -> {
                showGeneralError(
                    getString(error.toMessageRes())
                )
            }

            AuthError.EmailAlreadyExists -> {
                showGeneralError(
                    getString(error.toMessageRes())
                )
            }

            AuthError.Unknown -> {
                showGeneralError(
                    getString(error.toMessageRes())
                )
            }
        }
    }


    private fun showProgressLoading(isLoading: Boolean) {
        binding.btnRegisterId.isEnabled = !isLoading
        binding.progressRegisterId.isVisible = isLoading
        binding.btnRegisterId.text = if (isLoading) "" else getString(R.string.btn_register)
    }

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(
            loginUseCase = requireContext().appContainer.loginUseCase,
            registerUseCase = requireContext().appContainer.registerUseCase,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    // يتعامل مع عناصر الxml view
    {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeAuthState()
        observeAuthEvent()
    }

    private fun setupClickListeners() {
        binding.btnRegisterId.setOnClickListener {
            registerUser()
        }
        binding.btnNavToLoginIdRegisterId.setOnClickListener {
            findNavController().navigate(R.id.action_registerUser_to_loginUser)
        }
        binding.toolbarRegisterId.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.imgClearGeneralErrorRegisterId.setOnClickListener {
            hideGeneralError()
        }
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) // ابدا collect عمدما الشاشة ظاهرة و اوقف collect عندما الشاشة تختفي
            {
                viewModel.authState.collect { state ->
                    when (state) {
                        AuthState.Idle -> {
                            showProgressLoading(false)
                        }

                        AuthState.Loading -> {
                            clearFieldErrors()
                            hideGeneralError()
                            showProgressLoading(true)
                        }

                        is
                        AuthState.Error -> {
                            showProgressLoading(false)
                            showAuthError(state.error)
                        }
                    }
                }
            }
        }
    }

    private fun observeAuthEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authEvent.collect { event ->
                    when (event) {
                        AuthEvent.NavigationToHomeListChat -> {
                            findNavController().navigate(
                                R.id.list_chats_nav_graph,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.authentication_nav_host, true)
                                    .build()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun registerUser() {
        val txtName = binding.etNameRegisterId.text.toString().trim()
        val txtEmail = binding.etEmailRegisterId.text.toString().trim()
        val txtPassword = binding.etPasswordRegisterId.text.toString().trim()

        viewModel.registerUser(nameR = txtName, emailR = txtEmail, passwordR = txtPassword)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}