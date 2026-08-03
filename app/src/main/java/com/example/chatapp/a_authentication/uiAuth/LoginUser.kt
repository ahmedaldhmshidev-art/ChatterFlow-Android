package com.example.chatapp.a_authentication.uiAuth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentLoginUserBinding
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthError
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthState
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModel
import com.example.chatapp.a_authentication.viewModelAuth.AuthViewModelFactory
import com.example.chatapp.a_application.appContainer
import kotlinx.coroutines.launch

class LoginUser : Fragment() {


    private fun showToastMessage(text: AuthError) {
        Toast.makeText(requireContext(),getErrorMessage(text), Toast.LENGTH_SHORT).show()
    }


    private fun showProgressLoading(isLoading: Boolean) {
       binding.btnLoginId.isEnabled = !isLoading

        binding.progressLoginId.isVisible = isLoading

        binding.btnLoginId.text = if (isLoading) "" else getString(R.string.btn_login)

    }

    private fun getErrorMessage(error: AuthError):String {
        return when(error){
            AuthError.EmptyName ->
                getString(R.string.error_empty_name_validError)
            AuthError.EmptyEmail ->
                getString(R.string.error_empty_email_validError)
            AuthError.EmptyPassword->
                getString(R.string.error_empty_password_validError)
            AuthError.InvalidEmail ->
                getString(R.string.error_invalid_email_validError)
            AuthError.EmailAlreadyExists ->
                getString(R.string.error_email_already_exists_AuthError)
            AuthError.WeakPassword ->
                getString(R.string.error_weak_password_AuthError)
            AuthError.NetworkError ->
                getString(R.string.error_network_error_AuthError)
            AuthError.InvalidCredentials->
                getString(R.string.error_invalid_credentials_AuthError)
            AuthError.Unknown->
                getString(R.string.error_invalid_credentials_AuthError)
            else -> {
                getString(R.string.error_unknown_error_AuthError)
            }

        }

    }

    private var _binding :FragmentLoginUserBinding?=null
    private val binding get() =_binding!!

    private val viewModel : AuthViewModel by viewModels {
        AuthViewModelFactory(
            loginUseCase = requireContext().appContainer.loginUseCase ,
            registerUseCase = requireContext().appContainer.registerUseCase,
            logoutUseCase = requireContext().appContainer.logoutUseCase
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginUserBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListener()
        observeAuthState()
        observeAuthEvent()
    }

    private fun observeAuthEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.authEvent.collect{
                    when(it){
                        AuthEvent.NavigationToHomeListChat ->{
                            findNavController().navigate(R.id.list_chats_nav_graph,null ,
                                NavOptions.Builder().setPopUpTo(R.id.authentication_nav_host,true).build()
                            )
                        }
                        AuthEvent.NavigationToLogin ->{}
                    }

                }
            }
        }
    }


    private fun setupOnClickListener() {
        binding.btnLoginId.setOnClickListener {
            login()
        }
        binding.btnNavToRegisterIdLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginUser_to_registerUser)
        }
    }


    private fun login() {
        val txtEmail = binding.etEmailLoginId.text.toString().trim()
        val txtPassword = binding.etPasswordLoginId.text.toString().trim()
        viewModel.signInUser( email = txtEmail , password = txtPassword)
    }
    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.authState.collect{
                        state->
                    when(state){
                        AuthState.Idle->{
                          showProgressLoading(false)
                        }
                        AuthState.Loading->{
                            showProgressLoading(true)
                        }
                        is
                        AuthState.Error ->{
                            showProgressLoading(false)
                            showToastMessage(state.error)
                        }
                    }
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}