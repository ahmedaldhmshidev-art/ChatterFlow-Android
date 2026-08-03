package com.example.chatapp.c_listChatUser.accountMenu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.d_chat_Document.repository_document.ChatDocumentRepository
import com.example.chatapp.d_chat_Document.viewModel_document.ChatDocumentInfoViewModel
import com.example.chatapp.c_listChatUser.accountMenu.domin.GetAccountCurrentUseCase
import com.example.chatapp.c_listChatUser.accountMenu.uiState.AccountUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val getAccountCurrentUseCase: GetAccountCurrentUseCase
): ViewModel() {

    private val _accountStatus = MutableStateFlow(AccountUiState())
    val accountState = _accountStatus .asStateFlow()

    fun loadCurrentUser(){
        _accountStatus.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
             val currentUser =  getAccountCurrentUseCase()

                if (currentUser != null) {
                    _accountStatus.update {
                        it.copy(
                            isLoading = false,
                            user = currentUser,
                        )
                    }
                }
                else {
                    _accountStatus.update {
                        it.copy(
                            isLoading = false,
                            error = "failed to get current user "
                        )
                    }
                }
            }
        }


    fun updateProfileAccount(
        newName:String , newBio:String
    ){
        if (newName.isBlank()){
            _accountStatus.update {
                it.copy(
                    error = "Name cannot be empty"
                )
            }
            return
        }
        _accountStatus.update {
            it.copy(
                isSave = true ,
                error = null
            )
        }
    viewModelScope.launch {

    }
    }


    }


class AccountViewModelFactory(
    private val getAccountCurrentUseCase: GetAccountCurrentUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                getAccountCurrentUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}