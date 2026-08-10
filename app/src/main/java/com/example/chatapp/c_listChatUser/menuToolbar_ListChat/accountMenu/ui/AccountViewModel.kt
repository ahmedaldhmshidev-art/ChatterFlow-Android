package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.useCase.LogoutUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.ChangePasswordUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.GetAccountCurrentUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin.UpdateInfoAccountUseCase
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountEvent
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.AccountUiState
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.PasswordField
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState.ProfileField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getAccountCurrentUseCase: GetAccountCurrentUseCase,
    private val updateInfoAccountUseCase: UpdateInfoAccountUseCase,
    private val changePassword: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {


    private val _accountStatus = MutableStateFlow(AccountUiState())
    val accountState = _accountStatus.asStateFlow()

    private val _event = Channel<AccountEvent>()
    val event = _event.receiveAsFlow()


    fun loadCurrentUser() {
        viewModelScope.launch {

            _accountStatus.update { it.copy(isLoading = true) }
            //جلب البيانات من useCase
            val currentUser = getAccountCurrentUseCase()

            if (currentUser != null) {
                _accountStatus.update {
                    it.copy(
                        isLoading = false,
                        user = currentUser,
                    )
                }
            } else {
                _accountStatus.update {
                    it.copy(
                        isLoading = false,
                    )
                }
                _event.send(AccountEvent.Error("Failed to load account"))
            }
        }
    }


    fun updateProfileAccount(
        oldName: String, newName: String, newBio: String
    ) {
        if (newName.isBlank()) {
            viewModelScope.launch {
                _event.send(AccountEvent.ErrorProfile(field = ProfileField.NAME_BLANK))
            }
            return
        }
        if (newName == oldName) {
            viewModelScope.launch {
                _event.send(AccountEvent.ErrorProfile(field = ProfileField.NEW_NAME_MUST_BE_DIFFERENT))
            }
            return
        }

        viewModelScope.launch {
            val result = updateInfoAccountUseCase(newName = newName, newBio = newBio)

            result.onSuccess {
                _event.send(AccountEvent.SaveSuccessEditProfile)
                loadCurrentUser()
            }
                .onFailure {
                    _event.send(AccountEvent.Error(it.message ?: "Error to update account "))
                }
        }
    }

    fun changePassword(
        currentPassword: String, newPassword: String, confirmPassword: String
    ) {
        // Current password blank
        if (currentPassword.isBlank()) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(field = PasswordField.CURRENT_PASSWORD_BLANK)) }
            return
        }
        // new password blank
        if (newPassword.isBlank()) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(field = PasswordField.NEW_PASSWORD_BLANK)) }
            return
        }
        // newPassword less 6 number
        if (newPassword.length < 6) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(PasswordField.NEW_PASSWORD_TOO_SHORT_6)) }
            return
        }
        // كلمة المرور الجديدة مثل القديمة يجب ان تختلف
        if (newPassword == currentPassword) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(field = PasswordField.NEW_PASSWORD_MUST_BE_DIFFERENT_CURRENT)) }
            return
        }
        // كلمة مرور التحقق فاضية
        if (confirmPassword.isBlank()) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(field = PasswordField.CONFIRM_PASSWORD_BLANK)) }
            return
        }
        // اذا كانت كلمة المرور التاكيد لا تساوي الجديدة لايوجد تطابق
        if (confirmPassword != newPassword) {
            viewModelScope.launch { _event.send(AccountEvent.ErrorPassword(field = PasswordField.CONFIRM_PASSWORD_DOES_NOT_MATCH)) }
            return
        }

        viewModelScope.launch {
            _accountStatus.update { it.copy(isLoading = true) }

            try {
                val result = changePassword(currentPassword, newPassword)
                result.onSuccess {
                    _event.send(
                        AccountEvent.PasswordChangedSuccess
                    )
                }
                result.onFailure { error ->
                    _event.send(AccountEvent.Error(error.message ?: "Failed to change password"))
                }
            } catch (e: Exception) {
                _event.send(AccountEvent.Error(e.message ?: "Something went wrong"))

            } finally {
                _accountStatus.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
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
    private val changePassword: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                getAccountCurrentUseCase,
                updateInfoAccountUseCase,
                changePassword,
                logoutUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}