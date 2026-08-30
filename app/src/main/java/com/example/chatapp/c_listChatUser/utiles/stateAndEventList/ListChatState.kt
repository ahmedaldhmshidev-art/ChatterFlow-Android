package com.example.chatapp.c_listChatUser.utiles.stateAndEventList

import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatError
import com.example.chatapp.c_listChatUser.modelList.ListChatUsers

sealed interface ListChatState {
    data object Idle : ListChatState
    data object Loading : ListChatState
    data object Empty : ListChatState
    data class Success(val users: List<ListChatUsers>) : ListChatState

    data class SearchEmpty(val query: String) : ListChatState

    data class Error(val error: ListChatError) : ListChatState
}