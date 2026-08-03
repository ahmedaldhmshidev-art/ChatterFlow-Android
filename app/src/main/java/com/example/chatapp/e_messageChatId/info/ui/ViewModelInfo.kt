package com.example.chatapp.e_messageChatId.info.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chatapp.c_listChatUser.accountMenu.domin.GetAccountCurrentUseCase
import com.example.chatapp.c_listChatUser.accountMenu.ui.AccountViewModel
import com.example.chatapp.e_messageChatId.info.uiState.UiStateInfo
import com.example.chatapp.e_messageChatId.info.useCase.GetOtherUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelInfo(
    private val infoUserUseCase: GetOtherUserUseCase
):ViewModel() {

    private val _stateInfo = MutableStateFlow(UiStateInfo())
    val stateInfo = _stateInfo.asStateFlow()

    fun getOtherUser(otherUserId: String) {

        _stateInfo.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val otherUser = infoUserUseCase(otherUserId)

            if (otherUser!= null){
                _stateInfo.update { it.copy(isLoading = false , user = otherUser) }
            }
            else {
                _stateInfo.update { it.copy(isLoading = false, error = "other user null")
                }


            }
        }
    }
}

class InfoViewModelFactory(
    private val getOtherUserUseCase: GetOtherUserUseCase

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ViewModelInfo::class.java)) {
            return ViewModelInfo(
               getOtherUserUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}

