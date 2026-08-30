package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError.AccountError
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError.AccountErrorMapper
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.ChangePasswordUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.GetAccountCurrentUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.UpdateInfoAccountUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountEvent
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getAccountCurrentUseCase: GetAccountCurrentUseCase,
    private val updateInfoAccountUseCase: UpdateInfoAccountUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private fun setLoading(isLoading: Boolean) {
        _accountStatus.update { it.copy(isLoading = isLoading) }
    }

    fun clearError() {
        _accountStatus.update { it.copy(error = null) }
    }

    private fun setError(error: AccountError) {
        _accountStatus.update { it.copy(error = error) }
    }

    private val _accountStatus = MutableStateFlow(AccountUiState())
    val accountState = _accountStatus.asStateFlow()

    private val _event = Channel<AccountEvent>()
    val event = _event.receiveAsFlow()


    fun loadCurrentUser() {
        viewModelScope.launch {
            setLoading(true)
            clearError()
            //جلب البيانات من useCase
            val user = getAccountCurrentUseCase()
            if (user != null) {
                _accountStatus.update {
                    it.copy(isLoading = false, user = user)
                }
            } else {
                setLoading(false)
                setError(AccountError.General.Unknown)
            }
        }
    }

    fun updateProfileAccount(
        oldName: String, newName: String, newBio: String, oldBio: String
    ) {
        val name = newName.trim()
        val bio = newBio.trim()
        val nameChanged = name != oldName.trim()
        val bioChanged = bio != oldBio.trim()
        when {
            name.isBlank() -> {
                setError(AccountError.Field.NameBlank)
                return
            }

            !nameChanged && !bioChanged -> {
                viewModelScope.launch {
                    _event.send(AccountEvent.NoChangedProfile)
                }
                return
            }
        }
        viewModelScope.launch {
            setLoading(true)
            clearError()
            val result = updateInfoAccountUseCase(
                newName = name, newBio = bio
            )
            result.onSuccess {
                clearError()
                _event.send(
                    AccountEvent.ProfileUpdated
                )
                loadCurrentUser()
            }
            result.onFailure { exception ->
                setError(AccountErrorMapper.map(exception))
            }
            setLoading(false)
        }
    }

    fun changePassword(
        currentPassword: String, newPassword: String, confirmPassword: String
    ) {
        val currentP = currentPassword.trim()
        val newP = newPassword.trim()
        val confirmP = confirmPassword.trim()

        when {
            currentP.isBlank() -> {
                setError(AccountError.Field.CurrentPasswordBlank)
                return
            }

            newP.isBlank() -> {
                setError(AccountError.Field.NewPasswordBlank)
                return
            }

            newP.length < 6 -> {
                setError(AccountError.Field.NewPasswordTooShort)
                return
            }

            newP == currentP -> {
                setError(AccountError.Field.NewPasswordMustBeDifferent)
                return
            }

            confirmP.isBlank() -> {
                setError(AccountError.Field.ConfirmPasswordBlank)
                return
            }

            confirmP != newP -> {
                setError(AccountError.Field.ConfirmPasswordDoesNotMatch)
                return
            }
        }
        viewModelScope.launch {
            setLoading(true)
            clearError()
            val result = changePasswordUseCase(
                currentPassword = currentP,
                newPassword = newP,
            )
            result.onSuccess {
                clearError()
                _event.send(AccountEvent.PasswordChanged)
            }
            result.onFailure { exception: Throwable ->
                setError(AccountErrorMapper.map(exception))
            }
            setLoading(false)
        }
    }

    fun logout() {
        logoutUseCase()
        viewModelScope.launch {
            _event.send(AccountEvent.NavigationToLogin)
        }
    }
}

class AccountViewModelFactory(
    private val getAccountCurrentUseCase: GetAccountCurrentUseCase,
    private val updateInfoAccountUseCase: UpdateInfoAccountUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                getAccountCurrentUseCase,
                updateInfoAccountUseCase,
                changePasswordUseCase,
                logoutUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}