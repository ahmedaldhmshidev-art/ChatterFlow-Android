package com.example.chatapp.a_authentication.viewModelAuth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.errorHandler.AuthError
import com.example.chatapp.a_authentication.errorHandler.AuthErrorMapper
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthState
import com.example.chatapp.a_authentication.useCase.LoginUseCase
import com.example.chatapp.a_authentication.useCase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {
    private fun setError(error: AuthError){
        _authState.value = AuthState.Error(error)
    }
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    // create account
    fun registerUser(nameR: String, emailR: String, passwordR: String) {
        val name = nameR.trim()
        val email =emailR.trim()
        val password = passwordR.trim()
        when {
            name.isBlank() -> {
                setError(AuthError.EmptyName)
                return
            }
            email.isBlank() -> {
                setError(AuthError.EmptyEmail)
            }
            password.isBlank() ->{
                setError(AuthError.EmptyPassword)
                return
            }
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                registerUseCase(name = name, email = email, password = password)
                _authState.value = AuthState.Idle
                _authEvent.emit(AuthEvent.NavigationToHomeListChat)
            } catch (e: Exception) {
                setError(AuthErrorMapper.mapError(e))
            }
        }
    }

    // login
    fun signInUser(emailL: String, passwordL: String) {
        val email = emailL.trim()
        val password = passwordL.trim()
        when{
            email.isBlank()->{
                setError(AuthError.EmptyEmail)
                return
            }
            password.isBlank() ->{
                setError(AuthError.EmptyPassword)
                return
            }
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                loginUseCase.invoke(email = email, password = password)
                _authState.value = AuthState.Idle
                _authEvent.emit(AuthEvent.NavigationToHomeListChat)
            } catch (e: Exception) {
                setError(AuthErrorMapper.mapError(e))
            }
        }
    }
}

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                loginUseCase, registerUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}
