package com.example.chatapp.a_authentication.viewModelAuth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.repositoryAuth.AuthRepository
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthError
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthErrorMapper
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent
import com.example.chatapp.a_authentication.stateAndEventAuth.AuthState
import com.example.chatapp.a_authentication.useCase.CheckSessionUseCase
import com.example.chatapp.a_authentication.useCase.LoginUseCase
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.a_authentication.useCase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase ,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
//    private val checkSessionUseCase: CheckSessionUseCase,
):ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _authEvent = MutableSharedFlow<AuthEvent>()
    val authEvent = _authEvent.asSharedFlow()

    // create account
    fun registerUser(name: String, email: String, password: String) {
        if (name.isBlank()) {
            _authState.value = AuthState.Error(AuthError.EmptyName)
            return
        }
        if (email.isBlank()) {
            _authState.value = AuthState.Error(AuthError.EmptyEmail)
            return
        }
        if (password.isBlank()) {
            _authState.value = AuthState.Error(AuthError.EmptyPassword)
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                registerUseCase(name = name, email = email, password = password)

                _authState.value = AuthState.Idle

                _authEvent.emit(AuthEvent.NavigationToHomeListChat)

            } catch (e: Exception) {
                Log.e("REGISTER_ERROR ", "REGISTER FAILED", e)
                _authState.value = AuthState.Error(AuthErrorMapper.mapError(e))
                e.printStackTrace()
            }
        }
    }

    // login
    fun signInUser(email: String, password: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error(AuthError.EmptyEmail)
            return
        }
        if (password.isBlank()) {
            _authState.value = AuthState.Error(AuthError.EmptyPassword)
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                loginUseCase.invoke(email = email, password = password)

                _authState.value = AuthState.Idle
                _authEvent.emit(AuthEvent.NavigationToHomeListChat)
            } catch (e: Exception) {
                Log.e("REGISTER_ERROR ", "REGISTER FAILED", e)
                _authState.value = AuthState.Error(AuthErrorMapper.mapError(e))
                e.printStackTrace()
            }
        }
    }

    fun logoutUser() {
        logoutUseCase()
        viewModelScope.launch {
            _authEvent.emit(AuthEvent.NavigationToLogin)
        }
    }

//    fun checkUserSession(){
//        if (checkSessionUseCase()){
//           viewModelScope.launch {
//               _authEvent.emit(AuthEvent.NavigationToHomeListChat)
//           }
//        }else{
//            viewModelScope.launch {
//                _authEvent.emit(AuthEvent.NavigationToLogin)
//            }
//        }
//    }
//}
}

class AuthViewModelFactory(
    private val loginUseCase: LoginUseCase ,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel ::class.java)) {
            return AuthViewModel(
                loginUseCase, registerUseCase, logoutUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}
